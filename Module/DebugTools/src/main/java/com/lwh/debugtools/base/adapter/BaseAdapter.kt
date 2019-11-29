package com.lwh.debugtools.base.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lwh.debugtools.base.MyAsyncListDiffer
import com.lwh.debugtools.R
import com.lwh.debugtools.base.context.ContextWrap
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.structure.GroupStructure
import java.lang.reflect.ParameterizedType

/**
 * @author lwh
 * @Date 2019/10/19 14:55
 * @description adapter基类
 */
class BaseAdapter<DiffCallBack : BaseDiffCallBack> private constructor() : RecyclerView.Adapter<BaseViewHolder>(),
    View.OnClickListener {

    private val TYPE_HEADER = R.integer.type_header
    private val TYPE_FOOTER = R.integer.type_footer
    private val TYPE_CHILD = R.integer.type_child

    /**
     * 保存分组列表的组结构
     */
    var mStructures = ArrayList<GroupStructure>()

    /**
     * 数据是否发生变化。如果数据发生变化，要及时更新组结构。
     */
    private var isDataChanged: Boolean = false

    /**
     * 记录当前需要创建item的下标
     */
    private var mTempPosition: Int = 0


    /**
     * item
     */
    val groupItems: MutableList<BaseGroupItem> = ArrayList()

    /**
     * 布局id
     */
    private val layouts: MutableMap<Int, Int> = LinkedHashMap()

    /**
     * item下标
     */
    private val itemIndex: MutableMap<Int, Int> = LinkedHashMap()

    /**
     * 数据差异对比
     */
    private lateinit var mDiffer: MyAsyncListDiffer<Any>

    /**
     * 数据差异对比结束回调
     */
    private var onDifferEndListener: OnDifferEndListener? = null

    /**
     * 上下文对象
     */
    private lateinit var mContextWrap: ContextWrap


    companion object {

        const val REMOVE = 1
        const val ADD = 2
        const val REPLACE = 3
    }

    constructor(activity: Activity) : this() {
        mContextWrap = ContextWrap.of(activity)
        init()
    }

    constructor(fragment: Fragment) : this() {
        mContextWrap = ContextWrap.of(fragment)
        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        registerAdapterDataObserver(GroupDataObserver())
        val diffClass: Class<*>
        val type = this@BaseAdapter.javaClass
        if (type is ParameterizedType) {
            diffClass = type.actualTypeArguments[0] as Class<*>
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            diffClass = BaseDiffCallBack::class.java
        }

        try {
            val diffCallBack = diffClass.getDeclaredConstructor().newInstance() as BaseDiffCallBack
            mDiffer = MyAsyncListDiffer(this, diffCallBack)
            if (mContextWrap.getFragment() is OnDifferEndListener) {
                onDifferEndListener = mContextWrap.getFragment() as OnDifferEndListener
            }
            if (onDifferEndListener == null && mContextWrap.getActivity() is OnDifferEndListener) {
                onDifferEndListener = mContextWrap.getActivity() as OnDifferEndListener
            }
        } catch (e: Exception) {

        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        structureChanged()
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)

        //处理StaggeredGridLayout，保证组头和组尾占满一行。
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.layoutPosition)
        }
    }

    private fun isStaggeredGridLayout(holder: RecyclerView.ViewHolder): Boolean {
        val layoutParams = holder.itemView.layoutParams
        return layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams
    }

    private fun handleLayoutIfStaggeredGridLayout(holder: RecyclerView.ViewHolder, position: Int) {
        if (judgeType(position) == TYPE_HEADER || judgeType(position) == TYPE_FOOTER) {
            val p = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            p.isFullSpan = true
        }
    }

    override fun getItemViewType(position: Int): Int {
        mTempPosition = position

        val groupPosition = getGroupPositionForPosition(position)
        val type = judgeType(position)
        when (type) {
            TYPE_HEADER -> {
                return getHeaderViewType(groupPosition)
            }
            TYPE_FOOTER -> {
                return getFooterViewType(groupPosition)
            }
            TYPE_CHILD -> {
                val childPosition = getChildPositionForPosition(groupPosition, position)
                return getChildViewType(groupPosition, childPosition)
            }
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val position = mTempPosition
        val groupPosition = getGroupPositionForPosition(position)
        val type = judgeType(position)
        val groupItem = groupItems[groupPosition]
        val item = when (type) {
            TYPE_HEADER -> groupItem.headerItem!!
            TYPE_FOOTER -> groupItem.footerItem!!
            else -> groupItem.children[getChildPositionForPosition(groupPosition, position)]
        }
        val view = item.getView(parent, viewType)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        bindView(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindView(holder, position, null)
    }

    /**
     * 绑定数据
     */
    private fun bindView(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>?) {
        val type = judgeType(position)
        val groupPosition = getGroupPositionForPosition(position)
        when (type) {
            TYPE_HEADER -> {
                bindHeaderView(holder, position, groupPosition, payloads)
            }
            TYPE_FOOTER -> {
                bindFooterView(holder, position, groupPosition, payloads)
            }
            TYPE_CHILD -> {
                val childPosition = getChildPositionForPosition(groupPosition, position)
                bindChildView(holder, position, groupPosition, childPosition, payloads)
            }
        }

    }

    private fun bindHeaderView(holder: BaseViewHolder, position: Int, groupPosition: Int, payloads: MutableList<Any>?) {
        val item = groupItems[groupPosition].headerItem!!
        bindView(holder, item, position, payloads)
    }

    private fun bindFooterView(holder: BaseViewHolder, position: Int, groupPosition: Int, payloads: MutableList<Any>?) {
        val item = groupItems[groupPosition].footerItem!!
        bindView(holder, item, position, payloads)
    }

    private fun bindChildView(
        holder: BaseViewHolder,
        position: Int,
        groupPosition: Int,
        childPosition: Int,
        payloads: MutableList<Any>?
    ) {
        val item = groupItems[groupPosition].children[childPosition]
        bindView(holder, item, position, payloads)
    }

    private fun bindView(holder: BaseViewHolder, item: BaseItem, position: Int, payloads: MutableList<Any>?) {
        holder.itemView.setTag(R.id.adapter_item, item)
        item.onClick = this
        item.mContextWrap = mContextWrap
        if (payloads.isNullOrEmpty()) {
            item.updateView(holder, position)
        } else {
            item.updateView(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        if (isDataChanged) structureChanged()
        return count()
    }


    //<editor-fold defaultstate="collapsed" desc="数据操作">

    private fun initItems(newData: List<BaseGroupItem>):ArrayList<BaseItem>{
        val newItems = ArrayList<BaseItem>()
        newData.forEach {newGroupItems ->
            newItems.addAll(initItem(newGroupItems))
        }
        return newItems
    }

    private fun initItem(groupItem: BaseGroupItem):ArrayList<BaseItem>{
        val newItems = ArrayList<BaseItem>()
        groupItem.headerItem?.let {
            newItems.add(it)
        }
        groupItem.children.forEach{child->
            newItems.add(child)
        }
        groupItem.footerItem?.let {
            newItems.add(it)
        }
        return newItems
    }

    fun setList(newData: List<BaseGroupItem>) {
        val newItems = initItems(newData)
        groupItems.clear()
        groupItems.addAll(newData)
        mDiffer.submitList(ArrayList(newItems), Runnable {
            onDifferEndListener?.onDifferEnd(REPLACE)
        })
    }

    /**
     * 添加数据
     */
    fun addList(newData: List<BaseGroupItem>) {
        val oldItems = initItems(groupItems)
        val newItems = initItems(newData)
        val allData = ArrayList<BaseItem>()
        allData.addAll(oldItems)
        allData.addAll(newItems)
        groupItems.addAll(newData)
        mDiffer.submitList(allData, Runnable {
            onDifferEndListener?.onDifferEnd(ADD)
        })
    }

    fun addData(o: BaseGroupItem) {
        val oldItems = initItems(groupItems)
        val newItems = initItem(o)
        val newList = ArrayList<BaseItem>()
        newList.addAll(oldItems)
        newList.addAll(newItems)
        groupItems.add(o)
        mDiffer.submitList(newList, Runnable {
            onDifferEndListener?.onDifferEnd(ADD)
        })
    }

    /**
     *  TODO:group和child问题
     */
    fun addData(index: Int, o: BaseGroupItem) {
        val oldItems = initItems(groupItems)
        val newItems = initItem(o)
        val newList = ArrayList<BaseItem>()
        newList.addAll(oldItems)
        newList.addAll(index, newItems)
        groupItems.add(index, o)
        mDiffer.submitList(newList, Runnable {
            onDifferEndListener?.onDifferEnd(ADD)
        })
    }

    /**
     * 删除第position项 TODO:group和child问题
     */
    fun removeAt(position: Int) {
        try {
            val newData = ArrayList(groupItems)
            newData.removeAt(position)
            groupItems.removeAt(position)
            mDiffer.submitList(newData, Runnable {
                onDifferEndListener?.onDifferEnd(REMOVE)
            })
        } catch (e: Exception) {

        }

    }

    /**
     * 删除某一项 TODO:group和child问题
     */
    fun removeAt(o: Any) {
        try {
            val newData = ArrayList(groupItems)
            newData.remove(o)
            groupItems.remove(o)
            mDiffer.submitList(newData, Runnable {
                onDifferEndListener?.onDifferEnd(REMOVE)
            })
        } catch (e: Exception) {

        }

    }

    /**
     * 删除全部 TODO:group和child问题
     */
    fun removeAll() {
        try {
            groupItems.clear()
            mDiffer.submitList(ArrayList(), Runnable {
                onDifferEndListener?.onDifferEnd(REMOVE)
            })
        } catch (e: Exception) {

        }

    }

    @IntDef(value = [REMOVE, REPLACE, ADD])
    annotation class OperatingStatus

    open interface OnDifferEndListener {
        /**
         * 对比差异结束
         */
        fun onDifferEnd(@OperatingStatus status: Int)
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="刷新操作">

    /**
     * 通知数据列表刷新
     */
    fun notifyDataChanged() {
        isDataChanged = true
        notifyDataSetChanged()
    }

    /**
     * 通知一组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupChanged(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        val itemCount = countGroupItem(groupPosition)
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount,payload)
        }
    }

    /**
     * 通知多组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRangeChanged(groupPosition: Int, count: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        var itemCount = 0
        if (groupPosition + count <= mStructures.size) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size)
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount,payload)
        }
    }

    /**
     * 通知组头刷新
     *
     * @param groupPosition
     */
    fun notifyHeaderChanged(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        if (index >= 0) {
            notifyItemChanged(index,payload)
        }
    }

    /**
     * 通知组尾刷新
     *
     * @param groupPosition
     */
    fun notifyFooterChanged(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupFooter(groupPosition)
        if (index >= 0) {
            notifyItemChanged(index,payload)
        }
    }

    /**
     * 通知一组里的某个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildChanged(groupPosition: Int, childPosition: Int,payload: Any? = null) {
        val index = getPositionForChild(groupPosition, childPosition)
        if (index >= 0) {
            notifyItemChanged(index,payload)
        }
    }

    /**
     * 通知一组里的多个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeChanged(groupPosition: Int, childPosition: Int, count: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, childPosition)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                if (structure.childrenCount >= childPosition + count) {
                    notifyItemRangeChanged(index, count,payload)
                } else {
                    notifyItemRangeChanged(index, structure.childrenCount - childPosition,payload)
                }
            }
        }
    }

    /**
     * 通知一组里的所有子项刷新
     *
     * @param groupPosition
     */
    fun notifyChildrenChanged(groupPosition: Int,payload: Any? = null) {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, 0)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                notifyItemRangeChanged(index, structure.childrenCount,payload)
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除操作">

    /**
     * 通知所有数据删除
     */
    fun notifyDataRemoved() {
        notifyItemRangeRemoved(0, itemCount)
        mStructures.clear()
    }

    /**
     * 通知一组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRemoved(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        val itemCount = countGroupItem(groupPosition)
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount)
            notifyItemRangeChanged(index, getItemCount() - itemCount,payload)
            mStructures.removeAt(groupPosition)
        }
    }

    /**
     * 通知多组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    fun notifyGroupRangeRemoved(groupPosition: Int, count: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        var itemCount = 0
        if (groupPosition + count <= mStructures.size) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count)
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size)
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount)
            notifyItemRangeChanged(index, getItemCount() - itemCount,payload)
            mStructures.removeAt(groupPosition)
        }
    }

    /**
     * 通知组头删除
     *
     * @param groupPosition
     */
    fun notifyHeaderRemoved(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupHeader(groupPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index,payload)
            structure.hasHeader = false
        }
    }

    /**
     * 通知组尾删除
     *
     * @param groupPosition
     */
    fun notifyFooterRemoved(groupPosition: Int,payload: Any? = null) {
        val index = getPositionForGroupFooter(groupPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index,payload)
            structure.hasFooter = false
        }
    }

    /**
     * 通知一组里的某个子项删除
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildRemoved(groupPosition: Int, childPosition: Int,payload: Any? = null) {
        val index = getPositionForChild(groupPosition, childPosition)
        if (index >= 0) {
            val structure = mStructures[groupPosition]
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index,payload)
            structure.childrenCount = structure.childrenCount - 1
        }
    }

    /**
     * 通知一组里的多个子项删除
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeRemoved(groupPosition: Int, childPosition: Int, count: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, childPosition)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                val childCount = structure.childrenCount
                var removeCount = count
                if (childCount < childPosition + count) {
                    removeCount = childCount - childPosition
                }
                notifyItemRangeRemoved(index, removeCount)
                notifyItemRangeChanged(index, itemCount - removeCount,payload)
                structure.childrenCount = childCount - removeCount
            }
        }
    }

    /**
     * 通知一组里的所有子项删除
     *
     * @param groupPosition
     */
    fun notifyChildrenRemoved(groupPosition: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            val index = getPositionForChild(groupPosition, 0)
            if (index >= 0) {
                val structure = mStructures[groupPosition]
                val itemCount = structure.childrenCount
                notifyItemRangeRemoved(index, itemCount)
                notifyItemRangeChanged(index, getItemCount() - itemCount,payload)
                structure.childrenCount = 0
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入操作">

    /**
     * 通知一组数据插入
     *
     * @param groupPosition
     */
    fun notifyGroupInserted(groupPosition: Int,payload: Any? = null) {
        var groupPosition = groupPosition
        val structure = GroupStructure(
            hasHeader(groupPosition),
            hasFooter(groupPosition), getChildrenCount(groupPosition)
        )
        if (groupPosition < mStructures.size) {
            mStructures.add(groupPosition, structure)
        } else {
            mStructures.add(structure)
            groupPosition = mStructures.size - 1
        }

        val index = countGroupRangeItem(0, groupPosition)
        val itemCount = countGroupItem(groupPosition)
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount)
            notifyItemRangeChanged(index + itemCount, getItemCount() - index,payload)
        }
    }

    /**
     * 通知多组数据插入
     *
     * @param groupPosition
     * @param count
     */
    fun notifyGroupRangeInserted(groupPosition: Int, count: Int,payload: Any? = null) {
        var groupPosition = groupPosition
        val list = ArrayList<GroupStructure>()
        for (i in 0 until count) {
            val structure = GroupStructure(
                hasHeader(i),
                hasFooter(i), getChildrenCount(i)
            )
            list.add(structure)
        }

        if (groupPosition < mStructures.size) {
            mStructures.addAll(groupPosition, list)
        } else {
            mStructures.addAll(list)
            groupPosition = mStructures.size - list.size
        }

        val index = countGroupRangeItem(0, groupPosition)
        val itemCount = countGroupRangeItem(groupPosition, count)
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount)
            notifyItemRangeChanged(index + itemCount, getItemCount() - index,payload)
        }
    }

    /**
     * 通知组头插入
     *
     * @param groupPosition
     */
    fun notifyHeaderInserted(groupPosition: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size && 0 > getPositionForGroupHeader(groupPosition)) {
            val structure = mStructures[groupPosition]
            structure.hasHeader = true
            val index = countGroupRangeItem(0, groupPosition)
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index,payload)
        }
    }

    /**
     * 通知组尾插入
     *
     * @param groupPosition
     */
    fun notifyFooterInserted(groupPosition: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size && 0 > getPositionForGroupFooter(groupPosition)) {
            val structure = mStructures[groupPosition]
            structure.hasFooter = true
            val index = countGroupRangeItem(0, groupPosition + 1)
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index,payload)
        }
    }

    /**
     * 通知一个子项到组里插入
     *
     * @param groupPosition
     * @param childPosition
     */
    fun notifyChildInserted(groupPosition: Int, childPosition: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            var index = getPositionForChild(groupPosition, childPosition)
            if (index < 0) {
                index = countGroupRangeItem(0, groupPosition)
                index += if (structure.hasHeader) 1 else 0
                index += structure.childrenCount
            }
            structure.childrenCount = structure.childrenCount + 1
            notifyItemInserted(index)
            notifyItemRangeChanged(index + 1, itemCount - index,payload)
        }
    }

    /**
     * 通知一组里的多个子项插入
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    fun notifyChildRangeInserted(groupPosition: Int, childPosition: Int, count: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            var index = countGroupRangeItem(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader) {
                index++
            }
            if (childPosition < structure.childrenCount) {
                index += childPosition
            } else {
                index += structure.childrenCount
            }
            if (count > 0) {
                structure.childrenCount = structure.childrenCount + count
                notifyItemRangeInserted(index, count)
                notifyItemRangeChanged(index + count, itemCount - index,payload)
            }
        }
    }

    /**
     * 通知一组里的所有子项插入
     *
     * @param groupPosition
     */
    fun notifyChildrenInserted(groupPosition: Int,payload: Any? = null) {
        if (groupPosition < mStructures.size) {
            var index = countGroupRangeItem(0, groupPosition)
            val structure = mStructures[groupPosition]
            if (structure.hasHeader) {
                index++
            }
            val itemCount = getChildrenCount(groupPosition)
            if (itemCount > 0) {
                structure.childrenCount = itemCount
                notifyItemRangeInserted(index, itemCount)
                notifyItemRangeChanged(index + itemCount, getItemCount() - index,payload)
            }
        }
    }
    //</editor-fold>

    inner class GroupDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            isDataChanged = true
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            isDataChanged = true
        }
    }


    override fun onClick(v: View?) {
        v?.let {
            var tag: Any? = it.getTag(R.id.adapter_item)
            var parent = it.parent
            if (tag == null && parent is View) {
                tag = parent.getTag(R.id.adapter_item)
            }
            if (tag is BaseItem) {
                tag.onClick(it)
            }
        }
    }


    private fun getGroupCount(): Int {
        return groupItems.size
    }

    private fun getChildrenCount(groupPosition: Int): Int {
        return groupItems[groupPosition].children.size
    }

    private fun hasHeader(groupPosition: Int): Boolean = groupItems[groupPosition].headerItem != null

    private fun hasFooter(groupPosition: Int): Boolean = groupItems[groupPosition].footerItem != null


    private fun getHeaderViewType(groupPosition: Int): Int {
        groupItems[groupPosition].headerItem?.let {
            return@getHeaderViewType it.getViewType()
        }
        return TYPE_HEADER
    }

    private fun getFooterViewType(groupPosition: Int): Int {
        groupItems[groupPosition].footerItem?.let {
            return@getFooterViewType it.getViewType()
        }
        return TYPE_FOOTER
    }

    private fun getChildViewType(groupPosition: Int, childPosition: Int): Int {
        val childrenItem = groupItems[groupPosition].children[childPosition]
        layouts[childrenItem.getViewType()] = childrenItem.layoutId()
        itemIndex[childrenItem.getViewType()] = childPosition
        return childrenItem.getViewType()
    }

    private fun count(): Int {
        return countGroupRangeItem(0, mStructures.size)
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    private fun judgeType(position: Int): Int {
        var itemCount = 0
        val groupCount = mStructures.size

        for (i in 0 until groupCount) {
            val structure = mStructures[i]
            if (structure.hasHeader) {
                itemCount += 1
                if (position < itemCount) {
                    return TYPE_HEADER
                }
            }

            itemCount += structure.childrenCount
            if (position < itemCount) {
                return TYPE_CHILD
            }

            if (structure.hasFooter) {
                itemCount += 1
                if (position < itemCount) {
                    return TYPE_FOOTER
                }
            }
        }

        throw IndexOutOfBoundsException(
            "can't determine the item type of the position." +
                    "position = " + position + ",item count = " + getItemCount()
        )
    }

    /**
     * 重置组结构列表
     */
    private fun structureChanged() {
        mStructures.clear()
        val groupCount = getGroupCount()
        for (i in 0 until groupCount) {
            mStructures.add(GroupStructure(hasHeader(i), hasFooter(i), getChildrenCount(i)))
        }
        isDataChanged = false
    }

    /**
     * 根据下标计算position所在的组（groupPosition）
     *
     * @param position 下标
     * @return 组下标 groupPosition
     */
    fun getGroupPositionForPosition(position: Int): Int {
        var count = 0
        val groupCount = mStructures.size
        for (i in 0 until groupCount) {
            count += countGroupItem(i)
            if (position < count) {
                return i
            }
        }
        return -1
    }

    /**
     * 根据下标计算position在组中位置（childPosition）
     *
     * @param groupPosition 所在的组
     * @param position      下标
     * @return 子项下标 childPosition
     */
    private fun getChildPositionForPosition(groupPosition: Int, position: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val itemCount = countGroupRangeItem(0, groupPosition + 1)
            val structure = mStructures[groupPosition]
            val p = structure.childrenCount - (itemCount - position) + if (structure.hasFooter) 1 else 0
            if (p >= 0) {
                return p
            }
        }
        return -1
    }

    /**
     * 获取一个组的组头下标 如果该组没有组头 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    fun getPositionForGroupHeader(groupPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val (hasHeader) = mStructures[groupPosition]
            return if (!hasHeader) {
                -1
            } else countGroupRangeItem(0, groupPosition)
        }
        return -1
    }

    /**
     * 获取一个组的组尾下标 如果该组没有组尾 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    private fun getPositionForGroupFooter(groupPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val (_, hasFooter) = mStructures[groupPosition]
            return if (!hasFooter) {
                -1
            } else countGroupRangeItem(0, groupPosition + 1) - 1
        }
        return -1
    }

    /**
     * 获取一个组指定的子项下标 如果没有 返回-1
     *
     * @param groupPosition 组下标
     * @param childPosition 子项的组内下标
     * @return 下标
     */
    private fun getPositionForChild(groupPosition: Int, childPosition: Int): Int {
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            if (structure.childrenCount > childPosition) {
                val itemCount = countGroupRangeItem(0, groupPosition)
                return itemCount + childPosition + if (structure.hasHeader) 1 else 0
            }
        }
        return -1
    }

    /**
     * 计算一个组里有多少个Item（头加尾加子项）
     *
     * @param groupPosition
     * @return
     */
    private fun countGroupItem(groupPosition: Int): Int {
        var itemCount = 0
        if (groupPosition >= 0 && groupPosition < mStructures.size) {
            val structure = mStructures[groupPosition]
            if (structure.hasHeader) {
                itemCount += 1
            }
            itemCount += structure.childrenCount
            if (structure.hasFooter) {
                itemCount += 1
            }
        }
        return itemCount
    }

    /**
     * 计算多个组的项的总和
     *
     * @return
     */
    private fun countGroupRangeItem(start: Int, count: Int): Int {
        var itemCount = 0
        val size = mStructures.size
        var i = start
        while (i < size && i < start + count) {
            itemCount += countGroupItem(i)
            i++
        }
        return itemCount
    }

    /**
     * 销毁
     */
    fun onDestroy() {
        mContextWrap.onDestroy()
    }

}