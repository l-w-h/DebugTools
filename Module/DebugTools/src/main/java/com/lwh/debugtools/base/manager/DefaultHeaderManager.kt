package com.lwh.debugtools.base.manager

import android.util.TypedValue
import android.view.View
import android.view.ViewStub
import androidx.core.content.ContextCompat
import com.lwh.debugtools.R
import kotlinx.android.synthetic.main.l_layout_header.view.*

/**
 * @author lwh
 * @Date 2019/10/21 15:24
 * @description 默认头部管理器
 */
class DefaultHeaderManager constructor(rootView: View,private val headerLayout: Int) : ViewManager(rootView), View.OnClickListener {

    init {
        init(rootView)
    }

    //<editor-fold defaultstate="collapsed" desc="变量">
    /**
     * header View
     */
    private var headerView: View? = null

    /**
     * 返回方法
     */
    var returnClick: ((View) -> Unit)? = null
    /**
     * funcA方法
     */
    var funcAClick: ((View) -> Unit)? = null
    /**
     * funcB方法
     */
    var funcBClick: ((View) -> Unit)? = null
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="初始化">

    /**
     * 初始化
     */
    private fun init(rootView: View) {
        if (headerView == null) {
            val headerViewStub = rootView.findViewById<ViewStub>(R.id.header_view)
            headerViewStub.layoutResource = headerLayout
            headerView = headerViewStub.inflate()
            headerView?.let {
                it.tv_return.setOnClickListener(this)
                it.tv_func_a.setOnClickListener(this)
                it.tv_func_b.setOnClickListener(this)
            }

        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置标题">

    /**
     * 标题
     */
    var title: String? = null
        set(value) {
            field = value
            headerView?.let {
                it.tv_title.text = field
            }
        }

    /**
     * header背景资源
     */
    var headerBackgroundResource: Int? = null
        set(value) {
            field = value
            headerView?.let {
                if (value != null) {
                    it.setBackgroundResource(value)
                }
            }
        }

    /**
     * header背景颜色
     */
    var headerBackgroundColor: Int? = null
        set(value) {
            field = value
            headerView?.let {
                if (value != null) {
                    it.setBackgroundColor(value)
                }
            }
        }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置返回">

    /**
     * 设置功能键a 是否显示
     */
    fun setReturnVisible(visible: Int) {
        headerView?.let {
            it.tv_return.visibility = visible
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置功能键a">

    /**
     * 设置功能键a 是否显示
     */
    fun setFuncAText(resid: Int) {
        headerView?.tv_func_a?.setText(resid)
    }

    /**
     * 设置功能键a 是否显示
     */
    fun setFuncAText(text: CharSequence) {
        headerView?.tv_func_a?.text = text
    }

    /**
     * 设置功能键a 是否显示
     */
    fun setFuncAVisible(visible: Int) {
        headerView?.tv_func_a?.visibility = visible
    }

    /**
     * 设置功能键a 字体大小
     */
    fun setFuncASize(sp: Float) {
        headerView?.tv_func_a?.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    }

    /**
     * 设置功能键a 字体颜色
     */
    fun setFuncAColor(color: Int) {
        headerView?.tv_func_a?.setTextColor(ContextCompat.getColor(headerView!!.context, color))
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置功能键b">

    /**
     * 设置功能键a 是否显示
     */
    fun setFuncBText(resid: Int) {
        headerView?.tv_func_b?.setText(resid)
    }

    /**
     * 设置功能键a 是否显示
     */
    fun setFuncBText(text: CharSequence) {
        headerView?.tv_func_b?.text = text
    }

    /**
     * 设置功能键b 是否显示
     */
    fun setFuncBVisible(visible: Int) {
        headerView?.tv_func_b?.visibility = visible
    }

    /**
     * 设置功能键b 字体大小
     */
    fun setFuncBSize(sp: Float) {
        headerView?.tv_func_b?.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    }

    /**
     * 设置功能键b 字体颜色
     */
    fun setFuncBColor(color: Int) {
        headerView?.tv_func_b?.setTextColor(ContextCompat.getColor(headerView!!.context, color))
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="点击方法">

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_return -> {
                returnClick?.let {
                    it(v)
                }
            }
            R.id.tv_func_a -> {
                funcAClick?.let {
                    it(v)
                }
            }
            R.id.tv_func_b -> {
                funcBClick?.let {
                    it(v)
                }
            }
        }
    }

    //</editor-fold>

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
        headerView = null
        returnClick = null
        funcAClick = null
        funcBClick = null
    }
    //</editor-fold>

}