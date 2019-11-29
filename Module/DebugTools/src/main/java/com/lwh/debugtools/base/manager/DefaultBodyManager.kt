package com.lwh.debugtools.base.manager

import android.view.View
import android.view.ViewStub
import com.lwh.debugtools.R

/**
 * @author lwh
 * @Date 2019/10/21 16:28
 * @description DefaultBodyManager
 */
class DefaultBodyManager(rootView: View, bodyLayout: Int) : ViewManager(rootView) {

    init {
        init(rootView, bodyLayout)
    }

    /**
     * body View
     */
    var bodyView: View? = null

    /**
     * 初始化
     */
    private fun init(rootView: View, bodyLayout: Int) {
        if (bodyView == null) {
            val bodyViewStub = rootView.findViewById<ViewStub>(R.id.body_view)
            bodyViewStub.layoutResource = bodyLayout
            bodyView = bodyViewStub.inflate()
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
        bodyView = null
    }

    //</editor-fold>
}