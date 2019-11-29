package com.lwh.debugtools.base.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.lwh.debugtools.R
import com.lwh.debugtools.view.statusview.StatusBarView
import kotlinx.android.synthetic.main.l_layout_root.*
import kotlinx.android.synthetic.main.l_layout_root.view.*

/**
 * @author lwh
 * @Date 2019/10/19 11:57
 * @description BaseActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        initParam()
        initBefore()
        super.onCreate(savedInstanceState)
        setContentView(setLayoutId())
        rootView = root_view
        initHeader()
        initBody()
        initFooter()
        init()
        initAfter()
    }

    /**
     * 获取状态栏
     */
    protected fun getStatusBar():StatusBarView{
        return rootView.status_bar_view
    }

    /**
     * 加载页面布局文件
     * @return
     */
    protected open fun setLayoutId(): Int = R.layout.l_layout_root

    /**
     * 初始化前
     */
    protected open fun initBefore() {}

    /**
     * 初始化
     */
    protected abstract fun init()

    /**
     * 初始化后
     */
    protected open fun initAfter() {}

    /**
     * 初始化参数
     */
    protected open fun initParam() {}

    /**
     * 初始化 头部
     */
    abstract fun initHeader()

    /**
     * 初始化 中间部分
     */
    abstract fun initBody()

    /**
     * 初始化 尾部
     */
    abstract fun initFooter()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changedStatusBar()
    }

    /**
     * 修改状态栏
     */
    protected fun changedStatusBar(){
        val statusBarType = rootView.status_bar_view.statusBarType
        if (statusBarType == StatusBarView.StatusBarType.VERTICAL){
            //横屏时状态栏依旧在原来的位置
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                rootView.root_view.orientation = LinearLayout.VERTICAL
            }else{
                //横屏
                rootView.root_view.orientation = LinearLayout.HORIZONTAL
            }
        }else{
            //横屏时状态栏根据实际情况移动位置
            rootView.root_view.orientation = LinearLayout.VERTICAL
        }
        rootView.status_bar_view.refreshLayout()
    }
}