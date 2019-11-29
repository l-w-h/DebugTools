package com.lwh.debugtools.utils.whitecrash.info

import android.text.TextUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * CrashInfo
 */
class DeviceInfo {

    private var deviceName: String? = null
    private val buildVersion = CopyOnWriteArrayList<String>()
    private var customInfo: Map<String, String> = ConcurrentHashMap()

    fun getDeviceName(): String? {
        return deviceName
    }

    fun setDeviceName(deviceName: String): DeviceInfo {
        this.deviceName = deviceName
        return this
    }

    fun getBuildVersion(): List<String> {
        return buildVersion
    }

    fun addBuildVersion(version: String): DeviceInfo {
        buildVersion.add(version)
        return this
    }

    fun getCustomInfo(): Map<String, String> {
        return customInfo
    }

    fun setCustomInfo(customInfo: Map<String, String>): DeviceInfo {
        this.customInfo = customInfo
        return this
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj === this || obj !is DeviceInfo) {
            true
        } else TextUtils.equals(
            deviceName,
            obj.getDeviceName()
        )
    }

    override fun hashCode(): Int {
        return if (deviceName == null) {
            0
        } else deviceName!!.hashCode()
    }
}
