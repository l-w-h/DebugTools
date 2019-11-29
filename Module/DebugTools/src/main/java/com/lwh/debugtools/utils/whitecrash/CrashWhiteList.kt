package com.lwh.debugtools.utils.whitecrash

import com.lwh.debugtools.utils.whitecrash.info.CrashInfo
import com.lwh.debugtools.utils.whitecrash.info.DeviceInfo

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * CrashWhiteList
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
internal object CrashWhiteList {

    private val mCrashList = ArrayList<CrashInfo>()
    private val mDeviceList = HashSet<DeviceInfo>()
    var customInfo: Map<String, String> = ConcurrentHashMap()

    val crashList: List<CrashInfo>
        get() = mCrashList

    val deviceList: Set<DeviceInfo>
        get() {
            for (crashInfo in mCrashList) {
                if (crashInfo == null || crashInfo.getDeviceInfo() == null || crashInfo!!.getDeviceInfo()!!.isEmpty()
                ) {
                    continue
                }
                mDeviceList.addAll(crashInfo.getDeviceInfo()!!)
            }
            return mDeviceList
        }

    fun addWhiteCrash(crashInfo: CrashInfo) {
        mCrashList.add(crashInfo)
    }
}
