package com.lwh.debugtools.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author lwh
 * @Date 2019/10/19 12:28
 * @description 生命周期
 */
abstract class ActivityLifecycleListener : Application.ActivityLifecycleCallbacks {

    private var appCount: Int = 0
    /**
     * 是否运行在后台 true是
     */
    private var isRunInBackground: Boolean = false
    /**
     * 前后台监听
     */
    private var onAppStatusListener: OnAppStatusListener? = null

    /**
     * 生命周期监听
     */
    var onActivityLifeCycleListener: OnActivityLifeCycleListener? = null

    fun setOnAppStatusListener(onAppStatusListener: OnAppStatusListener) {
        this.onAppStatusListener = onAppStatusListener
    }

    override fun onActivityPaused(activity: Activity) {
        onActivityLifeCycleListener?.onPause(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        onActivityLifeCycleListener?.onStart(activity)
        appCount++
        if (isRunInBackground) {
            //从后台返回app
            back2App(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        onActivityLifeCycleListener?.onDestroy(activity)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {
        onActivityLifeCycleListener?.onStop(activity)
        appCount--
        if (appCount == 0) {
            //从app退到后台
            leaveApp(activity)
        }
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        onActivityLifeCycleListener?.onCreate(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        onActivityLifeCycleListener?.onResume(activity)
    }


    /**
     * 从后台回到前台需要执行的逻辑
     */
    protected fun back2App(activity: Activity) {
        isRunInBackground = false
        onAppStatusListener?.onFront(activity)
    }

    /**
     * 离开应用 压入后台或者退出应用
     */
    protected fun leaveApp(activity: Activity) {
        isRunInBackground = true
        onAppStatusListener?.onBack(activity)
    }

    interface OnActivityLifeCycleListener {
        /**
         * 创建
         */
        fun onCreate(activity: Activity);

        /**
         * 开始
         */
        fun onStart(activity: Activity)

        /**
         * 获得焦点
         */
        fun onResume(activity: Activity)

        /**
         * 失去焦点
         */
        fun onPause(activity: Activity)

        /**
         * 停止
         */
        fun onStop(activity: Activity)

        /**
         * 销毁
         */
        fun onDestroy(activity: Activity)

    }

    interface OnAppStatusListener {
        /**
         * 在前台
         */
        fun onFront(activity: Activity)

        /**
         * 在后台
         */
        fun onBack(activity: Activity)
    }

}