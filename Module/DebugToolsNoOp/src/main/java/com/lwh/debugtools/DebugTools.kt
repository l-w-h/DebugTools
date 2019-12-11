package com.lwh.debugtools

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.DrawableRes
import com.lwh.debugtools.crash.BACKGROUND_MODE_SILENT
import com.lwh.debugtools.crash.CrashConfig
import com.lwh.debugtools.interceptor.RecordInterceptor
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener
import com.lwh.debugtools.view.floatingview.MagnetViewListener
import okhttp3.Interceptor

/**
 * @author lwh
 * @Date 2019/10/19 12:19
 * @description DebugTools
 */
class DebugTools private constructor() {

    companion object {

        /**
         * 应用程序上下文
         */
        private lateinit var context: Application

        /**
         * DebugTools实例
         */
        private var INSTANCE: DebugTools? = null

        /**
         * 是否打开
         */
        var open = false

        fun getInstance(application: Application = context): DebugTools {
            if (INSTANCE == null) {
                this.context = application
                INSTANCE = DebugTools()
            }
            return INSTANCE!!
        }
    }

    //<editor-fold defaultstate="collapsed" desc="初始化">

    fun init(autoAdd: Boolean = false, magnetViewListener: MagnetViewListener? = null): DebugTools {
        return this
    }

    /**
     * 初始化异常监听捕获
     */
    fun initCrash(
        restartActivityClass: Class<out Activity>
    ): DebugTools {
        initCrash(
            BACKGROUND_MODE_SILENT,
            true,
            true,
            true,
            true,
            2000,
            0,
            restartActivityClass,
            null,
            null
        )
        return this
    }

    /**
     * 初始化异常监听捕获
     */
    fun initCrash(
        @CrashConfig.BackgroundMode backgroundMode: Int = BACKGROUND_MODE_SILENT,
        enabled: Boolean = true,
        showErrorDetails: Boolean = true,
        showRestartButton: Boolean = true,
        trackActivities: Boolean = true,
        minTimeBetweenCrashesMs: Int = 2000,
        @DrawableRes errorDrawable: Int? = 0,
        restartActivityClass: Class<out Activity>,
        errorActivityClass: Class<out Activity>? = null,
        eventListener: CrashConfig.EventListener? = null
    ): DebugTools {

        return this
    }

    /**
     * 开启崩溃拦截
     */
    fun startWhitecrashIntercept(interceptAll: Boolean = true, onCrashListener: OnCrashListener? = null): DebugTools {

        return this
    }

    /**
     * 关闭崩溃拦截
     */
    fun stopWhitecrashIntercept(): DebugTools {
        return this
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DebugView">
    /**
     * 添加DebugView
     */
    open fun attachDebugView(activity: Activity): DebugTools {
        return this
    }

    /**
     * 移出DebugView
     */
    fun detachDebugView(activity: Activity): DebugTools {
        return this
    }

    /**
     * 添加view到页面
     */
    fun addViewToWindow(): DebugTools {
        return this
    }

    /**
     * 设置DebugView监听
     */
    fun setDebugViewListener(magnetViewListener: MagnetViewListener): DebugTools {
        return this
    }

    /**
     * 是否是授权页面
     */
    private fun isAuthorizePage(context: Context): Boolean {
        return false
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="网络">
    /**
     * 获取网络拦截器
     */
    public fun getRecordInterceptor(): Interceptor = RecordInterceptor(context)

    /**
     * 添加忽略url
     */
    fun addIgnoreUrl(url:String): DebugTools{
        return this
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="log">
    /**
     * 保存log打印信息 Verbose
     */
    fun logV(tag: String, content: String) {
        logV(tag, content, 0)
    }

    /**
     * 保存log打印信息 Verbose
     */
    fun logV(tag: String, content: String, jumpStack: Int) {
        log(tag, content, "Verbose", jumpStack)
    }

    /**
     * 保存log打印信息 Debug
     */
    fun logD(tag: String, content: String) {
        logD(tag, content, 0)
    }

    /**
     * 保存log打印信息 Debug
     */
    fun logD(tag: String, content: String, jumpStack: Int) {
        log(tag, content, "Debug", jumpStack)
    }

    /**
     * 保存log打印信息 Info
     */
    fun logI(tag: String, content: String) {
        logI(tag, content, 0)
    }

    /**
     * 保存log打印信息 Info
     */
    fun logI(tag: String, content: String, jumpStack: Int) {
        log(tag, content, "Info", jumpStack)
    }

    /**
     * 保存log打印信息 Warn
     */
    fun logW(tag: String, content: String) {
        logW(tag, content, 0)
    }

    /**
     * 保存log打印信息 Warn
     */
    fun logW(tag: String, content: String, jumpStack: Int) {
        log(tag, content, "Warn", jumpStack)
    }

    /**
     * 保存log打印信息 Error
     */
    fun logE(tag: String, content: String) {
        logE(tag, content, 0)
    }

    /**
     * 保存log打印信息 Error
     */
    fun logE(tag: String, content: String, jumpStack: Int) {
        log(tag, content, "Error", jumpStack)
    }

    /**
     * 组装log信息
     */
    private fun log(tag: String, content: String, type: String, jumpStack: Int) {

    }


    //</editor-fold>
}