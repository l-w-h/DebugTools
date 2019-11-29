package com.lwh.debugtools.utils.whitecrash

import android.os.Build
import com.lwh.debugtools.utils.whitecrash.info.CrashInfo
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener
import java.util.*

/**
 * CrashWhiteListManager
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
object CrashWhiteListManager {

    private val OPPO_R9PLUSTMA = "OPPO/R9PlustmA"
    private val OPPO_R9PLUS = "OPPO/R9 Plus"

    private val VIVO_X3L = "vivo/vivo X3L"
    private val VIVO_X3F = "vivo/vivo X3F"

    private val INFO_BRAND = "BRAND"

    /**
     * Vivo 4.3 通知栏消息崩溃，ROM bug
     */
    private val remoteServiceException: CrashInfo
        get() = CrashInfo().setExceptionName("RemoteServiceException")
            .setExceptionInfo("Bad notification posted from package com.netease.newsreader.activity: Couldn't expand RemoteViews for: StatusBarNotification")

    /**
     * 空指针崩溃
     */
    private val nullPointerException: CrashInfo
        get() = CrashInfo().setExceptionName(NullPointerException::class.java.simpleName)

    fun start() {
        val deviceCustomInfo = HashMap<String, String>()
        deviceCustomInfo[INFO_BRAND] = Build.BRAND

        val customInfo = HashMap<String, String>()
        customInfo[INFO_BRAND] = Build.BRAND
        WhiteCrash.get()?.setCustomInfo(deviceCustomInfo)?.start()
//        WhiteCrash.get()?.catchCrash(
//            remoteServiceException
//                .addDeviceInfo(DeviceInfo().setDeviceName(VIVO_X3L).addBuildVersion("4.3"))
//                .addDeviceInfo(DeviceInfo().setDeviceName(VIVO_X3F).addBuildVersion("4.3").setCustomInfo(customInfo))
//        )?.catchCrash(
//            nullPointerException
//                .addDeviceInfo(
//                    DeviceInfo().setDeviceName(WhiteCrash.deviceName)
//                        .addBuildVersion(WhiteCrash.buildVersion)
//                )
//        )?.setCustomInfo(deviceCustomInfo)?.start()
    }

    fun setInterceptAll(interceptAll: Boolean) {
        WhiteCrash.get()?.setInterceptAll(interceptAll)
    }

    fun setOnCrashListener(onCrashListener:OnCrashListener?){
        WhiteCrash.get()?.setOnCrashListener(onCrashListener)
    }

    fun stop() {
        WhiteCrash.get()?.stop()
    }
}
