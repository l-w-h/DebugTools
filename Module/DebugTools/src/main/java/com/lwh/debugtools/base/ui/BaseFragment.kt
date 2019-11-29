package com.lwh.debugtools.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lwh.debugtools.R
import com.lwh.debugtools.view.statusview.StatusBarView
import kotlinx.android.synthetic.main.l_layout_root.view.*

/**
 * @author lwh
 * @Date 2019/10/19 14:52
 * @description BaseFragment
 */
abstract class BaseFragment :Fragment(){

    protected open lateinit var rootView: View
    private var isInitView = false
    protected var isInit = false
    private var isVisibleToUser = false

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(setLayoutId(), container, false)
        getStatusBar().visibility = View.GONE
        isInitView = true
        isCanInit()
        return rootView
    }

    /**
     * 获取状态栏
     */
    protected fun getStatusBar(): StatusBarView {
        return rootView.status_bar_view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见，获取该标志记录下来
        if (isVisibleToUser) {
            this.isVisibleToUser = true
            isCanInit()
        } else {
            this.isVisibleToUser = false
        }
    }

    private fun isCanInit() {
        //所以条件是view初始化完成并且对用户可见
        if (isInitView && isVisibleToUser) {
            initParam()
            initBefore()
            initHeader()
            initBody()
            initFooter()
            init()
            initAfter()

            isInit = true
            //防止重复加载数据
            isInitView = false
            isVisibleToUser = false
        }
    }

    /**
     * 加载页面布局文件
     * @return
     */
    protected open fun setLayoutId(): Int = R.layout.l_layout_root

    /**
     * 设置header布局
     */
    protected open fun setHeaderLayoutId():Int = 0

    /**
     * 初始化前
     */
    protected open fun initBefore(){}

    /**
     * 初始化
     */
    protected abstract fun init()

    /**
     * 初始化后
     */
    protected open fun initAfter(){}

    /**
     * 初始化参数
     */
    protected open fun initParam(){}

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
}