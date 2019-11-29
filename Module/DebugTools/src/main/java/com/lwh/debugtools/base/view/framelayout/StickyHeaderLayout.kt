package com.lwh.debugtools.base.view.framelayout

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lwh.debugtools.base.adapter.BaseAdapter
import com.lwh.debugtools.base.holder.BaseViewHolder

/**
 * @author lwh
 * @Date 2019/11/5 14:55
 * @description 头部吸顶布局。只要用StickyHeaderLayout包裹{@link RecyclerView},
 * 并且使用{@link BaseAdapter},就可以实现列表头部吸顶功能。
 * StickyHeaderLayout只能包裹RecyclerView，而且只能包裹一个RecyclerView。
 */
class StickyHeaderLayout : FrameLayout {

    /**
     * 列表布局
     */
    private lateinit var mRecyclerView: RecyclerView

    /**
     * 吸顶布局，用于承载吸顶布局
     */
    private lateinit var mStickyLayout: FrameLayout

    /**
     * 保存吸顶布局的缓存池。它以列表组头的viewType为key,ViewHolder为value对吸顶布局进行保存和回收复用。
     */
    private val mStickyViews = SparseArray<BaseViewHolder>()

    //用于在吸顶布局中保存viewType的key。
    private val VIEW_TAG_TYPE = -101

    //用于在吸顶布局中保存ViewHolder的key。
    private val VIEW_TAG_HOLDER = -102

    //记录当前吸顶的组。
    private var mCurrentStickyGroup = -1

    //是否吸顶。
    private var isSticky = true

    //是否已经注册了adapter刷新监听
    private var isRegisterDataObserver = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        require(!(childCount > 0 || child !is RecyclerView)) {
            //外界只能向StickyHeaderLayout添加一个RecyclerView,而且只能添加RecyclerView。
            "StickyHeaderLayout can host only one direct child --> RecyclerView"
        }
        super.addView(child, index, params)
        mRecyclerView = child
        addOnScrollListener()
        addStickyLayout()
    }

    /**
     * 添加滚动监听
     */
    private fun addOnScrollListener() {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // 在滚动的时候，需要不断的更新吸顶布局。
                if (isSticky) {
                    updateStickyView(false)
                }
            }
        })
    }

    /**
     * 添加吸顶容器
     */
    private fun addStickyLayout() {
        mStickyLayout = FrameLayout(context)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        mStickyLayout.layoutParams = lp
        super.addView(mStickyLayout, 1, lp)
    }

    /**
     * 更新吸顶布局。
     *
     * @param imperative 是否强制更新。
     */
    private fun updateStickyView(imperative: Boolean) {
        val adapter = mRecyclerView.adapter
        //只有RecyclerView的adapter是BaseAdapter<*>的时候，才会添加吸顶布局。
        if (adapter is BaseAdapter<*>) {
            registerAdapterDataObserver(adapter)
            //获取列表显示的第一个项。
            val firstVisibleItem = getFirstVisibleItem()
            //通过显示的第一个项的position获取它所在的组。
            val groupPosition = adapter.getGroupPositionForPosition(firstVisibleItem)

            //如果当前吸顶的组头不是我们要吸顶的组头，就更新吸顶布局。这样做可以避免频繁的更新吸顶布局。
            if (imperative || mCurrentStickyGroup != groupPosition) {
                mCurrentStickyGroup = groupPosition

                //通过groupPosition获取当前组的组头position。这个组头就是我们需要吸顶的布局。
                val groupHeaderPosition = adapter.getPositionForGroupHeader(groupPosition)
                if (groupHeaderPosition != -1) {
                    //获取吸顶布局的viewType。
                    val viewType = adapter.getItemViewType(groupHeaderPosition)

                    //如果当前的吸顶布局的类型和我们需要的一样，就直接获取它的ViewHolder，否则就回收。
                    var holder = recycleStickyView(viewType)

                    //标志holder是否是从当前吸顶布局取出来的。
                    val flag = holder != null

                    if (holder == null) {
                        //从缓存池中获取吸顶布局。
                        holder = getStickyViewByType(viewType)
                    }

                    if (holder == null) {
                        //如果没有从缓存池中获取到吸顶布局，则通过BaseAdapter<*>创建。
                        holder = adapter.onCreateViewHolder(mStickyLayout, viewType)
                        holder.itemView.setTag(VIEW_TAG_TYPE, viewType)
                        holder.itemView.setTag(VIEW_TAG_HOLDER, holder)
                    }

                    //通过BaseAdapter<*>更新吸顶布局的数据。
                    //这样可以保证吸顶布局的显示效果跟列表中的组头保持一致。
                    adapter.onBindViewHolder(holder, groupHeaderPosition)

                    //如果holder不是从当前吸顶布局取出来的，就需要把吸顶布局添加到容器里。
                    if (!flag) {
                        mStickyLayout.addView(holder.itemView)
                    }
                } else {
                    //如果当前组没有组头，则不显示吸顶布局。
                    //回收旧的吸顶布局。
                    recycle()
                }
            }

            //这是是处理第一次打开时，吸顶布局已经添加到StickyLayout，但StickyLayout的高依然为0的情况。
            if (mStickyLayout.childCount > 0 && mStickyLayout.height == 0) {
                mStickyLayout.requestLayout()
            }

            //设置mStickyLayout的Y偏移量。
            mStickyLayout.translationY = calculateOffset(adapter, firstVisibleItem, groupPosition + 1)
        }
    }

    /**
     * 注册adapter刷新监听
     */
    private fun registerAdapterDataObserver(adapter: BaseAdapter<*>) {
        if (!isRegisterDataObserver) {
            isRegisterDataObserver = true
            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    updateStickyViewDelayed()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    updateStickyViewDelayed()
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    updateStickyViewDelayed()
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    updateStickyViewDelayed()
                }

            })
        }
    }

    private fun updateStickyViewDelayed() {
        postDelayed({ updateStickyView(true) }, 100)
    }

    /**
     * 判断是否需要先回收吸顶布局，如果要回收，则回收吸顶布局并返回null。
     * 如果不回收，则返回吸顶布局的ViewHolder。
     * 这样做可以避免频繁的添加和移除吸顶布局。
     *
     * @param viewType
     * @return
     */
    private fun recycleStickyView(viewType: Int): BaseViewHolder? {
        if (mStickyLayout.childCount > 0) {
            val view = mStickyLayout.getChildAt(0)
            val type = view.getTag(VIEW_TAG_TYPE) as Int
            if (type == viewType) {
                return view.getTag(VIEW_TAG_HOLDER) as BaseViewHolder
            } else {
                recycle()
            }
        }
        return null
    }

    /**
     * 回收并移除吸顶布局
     */
    private fun recycle() {
        if (mStickyLayout.childCount > 0) {
            val view = mStickyLayout.getChildAt(0)
            mStickyViews.put(
                view.getTag(VIEW_TAG_TYPE) as Int,
                view.getTag(VIEW_TAG_HOLDER) as BaseViewHolder
            )
            mStickyLayout.removeAllViews()
        }
    }

    /**
     * 从缓存池中获取吸顶布局
     *
     * @param viewType 吸顶布局的viewType
     * @return
     */
    private fun getStickyViewByType(viewType: Int): BaseViewHolder? {
        return mStickyViews.get(viewType)
    }

    /**
     * 计算StickyLayout的偏移量。因为如果下一个组的组头顶到了StickyLayout，
     * 就要把StickyLayout顶上去，直到下一个组的组头变成吸顶布局。否则会发生两个组头重叠的情况。
     *
     * @param gAdapter
     * @param firstVisibleItem 当前列表显示的第一个项。
     * @param groupPosition    下一个组的组下标。
     * @return 返回偏移量。
     */
    private fun calculateOffset(
        gAdapter: BaseAdapter<*>,
        firstVisibleItem: Int,
        groupPosition: Int
    ): Float {
        val groupHeaderPosition = gAdapter.getPositionForGroupHeader(groupPosition)
        if (groupHeaderPosition != -1) {
            val index = groupHeaderPosition - firstVisibleItem
            if (mRecyclerView.childCount > index) {
                //获取下一个组的组头的itemView。
                val view = mRecyclerView.getChildAt(index)
                val off = view.y - mStickyLayout.height
                if (off < 0) {
                    return off
                }
            }
        }
        return 0f
    }

    /**
     * 获取当前第一个显示的item .
     */
    private fun getFirstVisibleItem(): Int {
        var firstVisibleItem = -1
        val layout = mRecyclerView.layoutManager
        if (layout != null) {
            if (layout is GridLayoutManager) {
                firstVisibleItem = layout.findFirstVisibleItemPosition()
            } else if (layout is LinearLayoutManager) {
                firstVisibleItem = layout.findFirstVisibleItemPosition()
            } else if (layout is StaggeredGridLayoutManager) {
                val firstPositions = IntArray(layout.spanCount)
                layout.findFirstVisibleItemPositions(firstPositions)
                firstVisibleItem = getMin(firstPositions)
            }
        }
        return firstVisibleItem
    }

    private fun getMin(arr: IntArray): Int {
        var min = arr[0]
        for (x in 1 until arr.size) {
            if (arr[x] < min)
                min = arr[x]
        }
        return min
    }

    /**
     * 是否吸顶
     *
     * @return
     */
    fun isSticky(): Boolean {
        return isSticky
    }

    /**
     * 设置是否吸顶。
     *
     * @param sticky
     */
    fun setSticky(sticky: Boolean) {
        if (isSticky != sticky) {
            isSticky = sticky
            if (mStickyLayout != null) {
                if (isSticky) {
                    mStickyLayout.visibility = View.VISIBLE
                    updateStickyView(false)
                } else {
                    recycle()
                    mStickyLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun computeVerticalScrollOffset(): Int {
        if (mRecyclerView != null) {
            try {
                val method = View::class.java.getDeclaredMethod("computeVerticalScrollOffset")
                method.isAccessible = true
                return method.invoke(mRecyclerView) as Int
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return super.computeVerticalScrollOffset()
    }


    override fun computeVerticalScrollRange(): Int {
        if (mRecyclerView != null) {
            try {
                val method = View::class.java.getDeclaredMethod("computeVerticalScrollRange")
                method.isAccessible = true
                return method.invoke(mRecyclerView) as Int
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return super.computeVerticalScrollRange()
    }

    override fun computeVerticalScrollExtent(): Int {
        if (mRecyclerView != null) {
            try {
                val method = View::class.java.getDeclaredMethod("computeVerticalScrollExtent")
                method.isAccessible = true
                return method.invoke(mRecyclerView) as Int
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return super.computeVerticalScrollExtent()
    }

    override fun scrollBy(x: Int, y: Int) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollBy(x, y)
        } else {
            super.scrollBy(x, y)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollTo(x, y)
        } else {
            super.scrollTo(x, y)
        }
    }

}