package com.lwh.debugtools.utils.whitecrash

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.lwh.debugtools.crash.CustomActivityOnCrash
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.ErrorTable
import com.lwh.debugtools.utils.whitecrash.info.CrashInfo
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener
import com.lwh.debugtools.view.floatingview.utils.LContext
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * CrashCatcher
 */
internal object CrashCatcher {

    val TAG = "CrashCatcher"
    var MATCH_ALL = false

    /**
     * 是否开启
     */
    private var isStart = false

    /**
     * 拦截所有崩溃
     */
    var interceptAll = false

    /**
     * 崩溃回调
     */
    var onCrashListener: OnCrashListener? = null

    @Synchronized
    fun start() {
        if (isStart) {
            return
        }
        if (MATCH_ALL || checkDevice()) {
            Log.i(TAG, "in the deviceL list")
            isStart = true
            startCatchLooper()
        }
    }

    private fun startCatchLooper() {
        Handler(Looper.getMainLooper()).post(Runnable {
            while (true) {
                if (!isStart) {
                    return@Runnable
                }
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    crash(e)
                }

            }
        })
    }

    private fun crash(e: Throwable) {
        Log.i(TAG, "loop crash:$e")
        val intercept = isStart && checkThrowableHit(e)
        saveCrash(e, intercept)
        if (intercept) {
            onCrashListener?.onCrash(e)
        } else {
            throw e
        }
    }

    private fun saveCrash(e: Throwable, intercept: Boolean) {
        val errorTable = ErrorTable()
        errorTable.crashDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US)

        val context = LContext.get()
        val pi = context.packageManager.getPackageInfo(context.packageName, 0)
        errorTable.buildDate = dateFormat.format(Date(pi.lastUpdateTime))

        val versionName = CustomActivityOnCrash.getVersionName(LContext.get())
        errorTable.buildVersion = versionName

        val device = CustomActivityOnCrash.getDeviceModelName()
        errorTable.device = device
        val throwable = e::class.java.name
        errorTable.throwable = throwable
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        var stackTraceString = sw.toString()
        val stackTrace = stackTraceString
        errorTable.stack = stackTrace

        val activityLog = CustomActivityOnCrash.getActivityLog()
        errorTable.userActions = activityLog
        errorTable.state = if (intercept) 1 else 0
        val id = DatabaseUtils.putError(errorTable)


    }

    @Synchronized
    fun stop() {
        isStart = false
    }

    private fun checkDevice(): Boolean {
        val deviceName = CrashInfoUtils.deviceName
        Log.i(TAG, "deviceName: $deviceName")
        val buildVersion = CrashInfoUtils.buildVersion
        val deviceList = CrashWhiteList.deviceList
        if (deviceList == null || deviceList.isEmpty()) {
            return true
        }
        for (deviceInfo in deviceList) {
            if (deviceInfo == null) {
                continue
            }
            if (CrashInfoUtils.valid<Any>(deviceName, deviceInfo.getDeviceName())
                && CrashInfoUtils.valid(buildVersion, deviceInfo.getBuildVersion())
                && CrashInfoUtils.valid(CrashWhiteList.customInfo, deviceInfo.getCustomInfo())
            ) {
                return true
            }
        }
        return false
    }

    private fun checkThrowableHit(throwable: Throwable?): Boolean {
        if (interceptAll) {
            return true
        }
        if (throwable == null || TextUtils.isEmpty(CrashInfoUtils.getThrowableName(throwable))) {
            return false
        }
        val crashList = CrashWhiteList.crashList
        if (crashList == null || crashList.isEmpty()) {
            return false
        }
        for (i in crashList.indices) {
            if (isThrowableHit(throwable, crashList[i])) {
                Log.i(TAG, "ThrowableHit: $throwable")
                return true
            }
        }
        return false
    }

    private fun isThrowableHit(throwable: Throwable?, crashInfo: CrashInfo?): Boolean {
        if (throwable == null || crashInfo == null) {
            return false
        }
        Log.i(
            TAG,
            "throwableName:" + CrashInfoUtils.getThrowableName(throwable) + " ,throwableMessage:" + throwable.message
        )
        var containsName = true
        if (!TextUtils.isEmpty(crashInfo.getExceptionName())) {
            containsName = CrashInfoUtils.getThrowableName(throwable).contains(crashInfo.getExceptionName()!!)
        }
        var containsMessage = true
        if (!TextUtils.isEmpty(crashInfo.getExceptionInfo())) {
            containsMessage =
                if (throwable.message == null) false else throwable.message!!.contains(crashInfo.getExceptionInfo()!!)
        }
        Log.i(TAG, "containsName:$containsName ,containsMessage:$containsMessage")
        return containsName && containsMessage
    }
}
