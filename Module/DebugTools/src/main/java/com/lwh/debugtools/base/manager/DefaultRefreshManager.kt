package com.lwh.debugtools.base.manager

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewStub
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lwh.debugtools.R
import com.lwh.debugtools.base.adapter.BaseAdapter
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.listener.RefreshListener
import com.lwh.debugtools.base.mvp.BasePageModel
import com.lwh.debugtools.base.ui.BaseActivity
import com.lwh.debugtools.base.ui.BaseFragment
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @author lwh
 * @Date 2019/10/21 15:42
 * @description 默认刷新布局管理器
 */
open class DefaultRefreshManager<DiffCallBack : BaseDiffCallBack> constructor(rootView: View) :
    ViewManager(rootView), RefreshListener {

    var bodyLayout: Int = 0

    protected var bodyView: View? = null
    /**
     * 刷新布局
     */
    private var refreshLayout: SmartRefreshLayout? = null
    /**
     * 列表布局
     */
    private var recyclerView: RecyclerView? = null
    /**
     * 适配器
     */
    lateinit var adapter: BaseAdapter<DiffCallBack>
    /**
     * 分页模型
     */
    open val pageModel: BasePageModel = BasePageModel()

    /**
     * 加载数据方法
     */
    lateinit var loadData: () -> Unit


    constructor(activity: BaseActivity, rootView: View, bodyLayout: Int, loadData: () -> Unit) : this(rootView) {
        this.loadData = loadData
        this.bodyLayout = bodyLayout
        init(activity, rootView)
    }

    constructor(fragment: BaseFragment, rootView: View, bodyLayout: Int, loadData: () -> Unit) : this(rootView) {
        this.loadData = loadData
        this.bodyLayout = bodyLayout
        init(fragment, rootView)
    }


    /**
     * 刷新数据
     */
    override fun onRefresh() {
        pageModel.page = 1
        loadData()
    }

    /**
     * 加载下一页数据
     */
    override fun onLoadMore() {
        pageModel.page++
        loadData()
    }


    /**
     * 设置布局管理器
     */
    override fun setLayoutManager(context: Context): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * 加载结束，头部和尾部恢复初始样式
     */
    override fun loadEnd() {
        refreshLayout?.let {
            it.setEnableLoadMore(pageModel.pageNext)
            it.finishLoadMore()
            it.finishRefresh()
        }
    }

    protected open fun init(context: Any, rootView: View) {
        if (bodyView == null) {
            val bodyViewStub = rootView.findViewById<ViewStub>(R.id.body_view)
            bodyViewStub.layoutResource = bodyLayout
            bodyView = bodyViewStub.inflate()
        }
        refreshLayout = bodyView?.findViewById(R.id.refreshLayout)
        recyclerView = bodyView?.findViewById(R.id.recyclerView)
        refreshLayout?.let {
            it.setRefreshHeader(ClassicsHeader(rootView.context))
            it.setRefreshFooter(ClassicsFooter(rootView.context))
            it.setPrimaryColorsId(android.R.color.transparent, R.color.black)//全局设置主题颜色
            it.setEnableLoadMoreWhenContentNotFull(false)
            it.autoRefresh()
            it.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    onLoadMore()
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    onRefresh()
                }
            })
        }
        if (context is Activity) {
            adapter = BaseAdapter(context)
        } else if (context is Fragment) {
            adapter = BaseAdapter(context)
        } else {
            return
        }
        recyclerView?.let {
            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = setLayoutManager(rootView.context)
        }
    }

    //<editor-fold defaultstate="collapsed" desc="生命周期">
    /**
     * 创建
     */
    override fun onCreate() {

    }

    /**
     * 销毁
     */
    override fun onDestroy() {
        adapter.onDestroy()
        bodyView = null
        refreshLayout = null
        recyclerView = null
    }
    //</editor-fold>
}