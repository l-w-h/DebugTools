package com.lwh.debugtools.base.ui

import com.lwh.debugtools.R
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.manager.DefaultStickyRefreshManager

/**
 * @author lwh
 * @Date 2019/10/21 9:12
 * @description  吸顶+刷新Activity 布局内需要refreshLayout和recyclerView
 */
abstract class StickyRefreshActivity<DiffCallBack : BaseDiffCallBack> : RefreshActivity<DiffCallBack>() {

    //<editor-fold defaultstate="collapsed" desc="初始化">
    /**
     * 初始化 中间部分
     */
    override fun initBody() {
        if (bodyLayout == null) {
            bodyLayout = R.layout.l_layout_sticky_body
        }
        refreshManager = DefaultStickyRefreshManager(this, rootView, bodyLayout!!, ::loadData)
        refreshManager.onCreate()
    }

    //</editor-fold>

    /**
     * 设置是否吸顶 默认：true
     */
    fun setSticky(sticky: Boolean) {
        if (refreshManager is DefaultStickyRefreshManager) {
            (refreshManager as DefaultStickyRefreshManager<DiffCallBack>).isSticky = sticky
        }
    }
}