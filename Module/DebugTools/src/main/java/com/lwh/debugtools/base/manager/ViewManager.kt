package com.lwh.debugtools.base.manager

import android.view.View

/**
 * @author lwh
 * @Date 2019/10/21 15:42
 * @description ViewManager
 */
abstract class ViewManager(rootView: View){

    /**
     * 创建
     */
    abstract fun onCreate()

    /**
     * 销毁
     */
    abstract fun onDestroy()
}