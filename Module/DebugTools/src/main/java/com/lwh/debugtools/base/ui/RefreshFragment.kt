package com.lwh.debugtools.base.ui

import com.lwh.debugtools.R
import com.lwh.debugtools.base.adapter.BaseAdapter
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.manager.DefaultRefreshManager

/**
 * @author lwh
 * @Date 2019/10/19 14:54
 * @description 刷新Fragment 布局内需要refreshLayout和recyclerView
 */
abstract class RefreshFragment<DiffCallBack : BaseDiffCallBack> : HeaderFooterFragment(),
    BaseAdapter.OnDifferEndListener {

    //<editor-fold defaultstate="collapsed" desc="分页">

    lateinit var refreshManager: DefaultRefreshManager<DiffCallBack>

    /**
     * 刷新
     */
    open fun refresh() {
        loadData(1, refreshManager.adapter.itemCount)
    }

    /**
     * 重新加载数据
     */
    open fun reLoadData() {
        refreshManager.pageModel.reset()
        loadData()
    }

    /**
     * 加载数据
     */
    fun loadData() {
        loadData(refreshManager.pageModel.page, refreshManager.pageModel.pageSize)
    }

    /**
     * 加载数据
     */
    open fun loadData(page: Int, pageSize: Int) {

    }

    /**
     * 对比差异结束
     */
    override fun onDifferEnd(@BaseAdapter.OperatingStatus status: Int) {
        loadEnd()
    }


    /**
     * 加载结束，头部和尾部恢复初始样式
     */
    open fun loadEnd() {
        refreshManager.loadEnd()
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="初始化">

    /**
     * 初始化 中间部分
     */
    override fun initBody() {
        if (bodyLayout == null) {
            bodyLayout = R.layout.l_layout_body
        }
        refreshManager = DefaultRefreshManager(this, rootView, bodyLayout!!, ::loadData)
        refreshManager.onCreate()
    }

    /**
     * 初始化 尾部
     */
    override fun initFooter() {}
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="生命周期">

    override fun onDestroy() {
        super.onDestroy()
        if (isInit) {
            refreshManager.onDestroy()
        }
    }

    //</editor-fold>

}