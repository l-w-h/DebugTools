package com.lwh.debugtools.lifecycle

import android.app.Application

/**
 * @author lwh
 * @Date 2019/10/19 12:29
 * @description 注册和反注册生命周期监听
 */
class AppFrontBackHelper {
    /**
     * 前后台切换监听
     */
    var onAppStatusListener: ActivityLifecycleListener.OnAppStatusListener? = null
    /**
     * 生命周期
     */
    private val activityLifecycleListener: ActivityLifecycleListener by lazy { ActivityLifecycleListenerIml() }

    fun register(application: Application, onAppStatusListener: ActivityLifecycleListener.OnAppStatusListener) {
        activityLifecycleListener.setOnAppStatusListener(onAppStatusListener)
        application.registerActivityLifecycleCallbacks(activityLifecycleListener)
        this.onAppStatusListener = onAppStatusListener
    }

    fun unregister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleListener)
    }
}