package com.lwh.debugtools

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.lwh.debugtools.crash.BACKGROUND_MODE_SILENT
import com.lwh.debugtools.crash.CrashConfig
import com.lwh.debugtools.db.DatabaseOpenHelper
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.LogTable
import com.lwh.debugtools.interceptor.RecordInterceptor
import com.lwh.debugtools.lifecycle.ActivityLifecycleListener
import com.lwh.debugtools.lifecycle.ActivityLifecycleListenerIml
import com.lwh.debugtools.ui.activity.home.DTHomeActivity
import com.lwh.debugtools.utils.whitecrash.CrashWhiteListManager
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener
import com.lwh.debugtools.view.floatingview.FloatingMagnetView
import com.lwh.debugtools.view.floatingview.FloatingView
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
         * 自动添加
         */
        private var autoAdd = false

        /**
         * 储存打印数据时，获取当前调用任务堆栈信息，需要跳过的堆栈数
         */
        private var JUMP_STACK: Int = 3

        /**
         * 是否打开
         */
        var open = false

        /**
         * 忽略不拦截url
         */
        val ignoreUrls = ArrayList<String>()

        fun getInstance(application: Application = context): DebugTools {
            if (INSTANCE == null) {
                this.context = application
                INSTANCE = DebugTools()
            }
            DatabaseOpenHelper.getInstance(application)
            return INSTANCE!!
        }
    }

    //<editor-fold defaultstate="collapsed" desc="初始化">

    fun init(autoAdd: Boolean = false, magnetViewListener: MagnetViewListener? = null): DebugTools {
//        AppFrontBackHelper().register(context, object : ActivityLifecycleListener.OnAppStatusListener {
//
//            override fun onFront(activity: Activity) {
//            }
//
//            override fun onBack(activity: Activity) {
//            }
//
//        })
        val activityLifecycleListenerIml = ActivityLifecycleListenerIml()
        activityLifecycleListenerIml.onActivityLifeCycleListener =
            object : ActivityLifecycleListener.OnActivityLifeCycleListener {
                override fun onCreate(activity: Activity) {
                }

                override fun onStart(activity: Activity) {
                    if (!activity.javaClass.name.contains("com.lwh.debugtools.")) {
                        attachDebugView(activity)
                    }
                }

                override fun onResume(activity: Activity) {
                }

                override fun onPause(activity: Activity) {
                }

                override fun onStop(activity: Activity) {
                    detachDebugView(activity)
                }

                override fun onDestroy(activity: Activity) {
                }

            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.registerActivityLifecycleCallbacks(activityLifecycleListenerIml)
        }
        addViewToWindow()
        setDebugViewListener(magnetViewListener
            ?: object : MagnetViewListener {
                override fun onRemove(magnetView: FloatingMagnetView) {

                }

                override fun onClick(magnetView: FloatingMagnetView) {
                    DTHomeActivity.startActivity(magnetView.context)
                }
            })
        return this
    }

    /**
     * 初始化异常监听捕获
     */
    fun initCrash(
        restartActivityClass: Class<out Activity>
    ): DebugTools {
        initCrash(
            //BACKGROUND_MODE_SILENT,
            true,
            true,
            true,
            true,
            2000,
            0,
            restartActivityClass,
            null,
            null)
        return this
    }

    /**
     * 初始化异常监听捕获
     */
    fun initCrash(
       // @CrashConfig.BackgroundMode backgroundMode: Int = BACKGROUND_MODE_SILENT,
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
        val builder: CrashConfig.Builder = CrashConfig.Builder.create()
            .backgroundMode(BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
            .enabled(enabled) //是否启动全局异常捕获
            .showErrorDetails(showErrorDetails) //是否显示错误详细信息
            .showRestartButton(showRestartButton) //是否显示重启按钮
            .trackActivities(trackActivities) //是否跟踪Activity
            .minTimeBetweenCrashesMs(minTimeBetweenCrashesMs) //崩溃的间隔时间(毫秒)
            .errorDrawable(errorDrawable) //错误图标
            .restartActivity(restartActivityClass) //重新启动后的activity
        errorActivityClass?.let {
            builder.errorActivity(errorActivityClass)//崩溃后的错误activity
        }
        eventListener?.let {
            builder.eventListener(eventListener) //崩溃后的错误监听
        }
        builder.apply()
        return this
    }

    /**
     * 开启崩溃拦截
     */
    fun startWhitecrashIntercept(interceptAll: Boolean = true,onCrashListener: OnCrashListener? = null): DebugTools {
        CrashWhiteListManager.setInterceptAll(interceptAll)
        CrashWhiteListManager.setOnCrashListener(onCrashListener)
        CrashWhiteListManager.start()
        return this
    }

    /**
     * 关闭崩溃拦截
     */
    fun stopWhitecrashIntercept(): DebugTools {
        CrashWhiteListManager.stop()
        return this
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DebugView">
    /**
     * 添加DebugView
     */
    open fun attachDebugView(activity: Activity): DebugTools {
        FloatingView.get().attach(activity)
        return this
    }

    /**
     * 移出DebugView
     */
    fun detachDebugView(activity: Activity): DebugTools {
        FloatingView.get().detach(activity)
        return this
    }

    /**
     * 添加view到页面
     */
    private fun addViewToWindow(): DebugTools {
        FloatingView.get().add()
        return this
    }

    /**
     * 设置DebugView监听
     */
    fun setDebugViewListener(magnetViewListener: MagnetViewListener): DebugTools {
        FloatingView.get().listener(magnetViewListener)
        return this
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="网络">
    /**
     * 获取网络拦截器
     */
    fun getRecordInterceptor(): Interceptor = RecordInterceptor(context)
    /**
     * 获取网络拦截器
     */
    fun getRecordInterceptor(callback: RecordInterceptor.OnDecryptCallback?): Interceptor = RecordInterceptor(context,callback)

    /**
     * 添加忽略url
     */
    fun addIgnoreUrl(url:String): DebugTools{
        ignoreUrls.add(url)
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
        val logTable = LogTable()
        logTable.time = System.currentTimeMillis()
        logTable.content = content
        logTable.type = type
        logTable.tag = tag
        setLogStack(logTable, jumpStack + JUMP_STACK)
        DatabaseUtils.putLog(logTable)
    }

    /**
     * 设置堆栈信息
     */
    private fun setLogStack(logTable: LogTable, jumpStack: Int) {
        val stackTraceElements = Thread.currentThread().stackTrace
        var position = -1
        val stringBuilder = StringBuilder()
        stackTraceElements.forEachIndexed { index, stackTraceElement ->
            if (TextUtils.equals(stackTraceElement.fileName, "${javaClass.simpleName}.kt") and TextUtils.equals(
                    stackTraceElement.methodName,
                    "setLogStack"
                )
            ) {
                position = index + jumpStack
            }
            if (position == index) {
                logTable.location = stackTraceElement.toString()
            }
            stringBuilder.append(stackTraceElement.toString())
                .append("\n")
        }
        logTable.logStack = stringBuilder.toString()
    }
    //</editor-fold>
}