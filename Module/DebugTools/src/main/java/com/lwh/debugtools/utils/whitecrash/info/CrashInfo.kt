package com.lwh.debugtools.utils.whitecrash.info

import java.util.concurrent.CopyOnWriteArrayList

/**
 * CrashInfo
 */
class CrashInfo {

    private var exceptionName: String? = null
    private var exceptionInfo: String? = null
    private var deviceInfo: MutableList<DeviceInfo>? = null

    fun getExceptionInfo(): String? {
        return exceptionInfo
    }

    fun setExceptionInfo(exceptionInfo: String): CrashInfo {
        this.exceptionInfo = exceptionInfo
        return this
    }

    fun getExceptionName(): String? {
        return exceptionName
    }

    fun setExceptionName(exceptionName: String): CrashInfo {
        this.exceptionName = exceptionName
        return this
    }

    fun getDeviceInfo(): List<DeviceInfo>? {
        return deviceInfo
    }

    fun addDeviceInfo(deviceInfo: DeviceInfo): CrashInfo {
        if (this.deviceInfo == null) {
            this.deviceInfo = CopyOnWriteArrayList()
        }
        this.deviceInfo!!.add(deviceInfo)
        return this
    }
}
