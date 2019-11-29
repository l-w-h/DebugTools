package com.lwh.debugtools.base.ui

import android.view.View
import com.lwh.debugtools.R
import com.lwh.debugtools.base.manager.DefaultBodyManager
import com.lwh.debugtools.base.manager.DefaultHeaderManager
import kotlinx.android.synthetic.main.l_layout_root.*

/**
 * @author lwh
 * @Date 2019/10/21 16:15
 * @description 有页头和页脚的页面
 */
abstract class HeaderFooterFragment : BaseFragment() {

    //<editor-fold defaultstate="collapsed" desc="布局管理器">
    protected open var headerManager: DefaultHeaderManager? = null
    protected open lateinit var bodyManager: DefaultBodyManager
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="布局变量">

    var headerLayout: Int = R.layout.l_layout_header
    protected var bodyLayout: Int? = null

    /**
     * 是否显示头部View 默认显示
     */
    protected open var showHeaderView: Boolean = true
    //</editor-fold>

    /**
     * 初始化 头部
     */
    override fun initHeader() {
        if (showHeaderView && headerManager == null) {
            headerManager = DefaultHeaderManager(root_view, headerLayout)
            headerManager?.onCreate()
            headerManager?.let {
                it.returnClick = ::returnClick
                it.funcAClick = ::funcAClick
                it.funcBClick = ::funcBClick
            }
        }
    }

    /**
     * 初始化 中间部分
     */
    override fun initBody() {
        if (bodyLayout == null) {
            throw RuntimeException("body layout can't be null")
        }
        bodyManager = DefaultBodyManager(rootView, bodyLayout!!)
    }


    /**
     * 初始化 尾部
     */
    override fun initFooter(){

    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="点击头部监听">
    /**
     * 点击返回
     */
    open fun returnClick(v: View) {
        activity?.finish()
    }

    /**
     * 点击FuncA
     */
    open fun funcAClick(v: View) {

    }

    /**
     * 点击FuncB
     */
    open fun funcBClick(v: View) {

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="生命周期">

    override fun onDestroy() {
        super.onDestroy()
        headerManager?.onDestroy()
    }
    //</editor-fold>

}