package com.lwh.debugtools.utils.whitecrash

import android.os.Build
import android.text.TextUtils
import java.util.Locale
import java.util.regex.Pattern

/**
 * CrashInfoUtils
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
internal object CrashInfoUtils {

    private val FORWARD_SLASH_REGEX = Pattern.quote("/")

    val deviceName: String
        get() = String.format(
            Locale.US,
            "%s/%s",
            removeForwardSlashesIn(Build.MANUFACTURER),
            removeForwardSlashesIn(Build.MODEL)
        )

    val buildVersion: String
        get() = removeForwardSlashesIn(Build.VERSION.RELEASE)

    private fun removeForwardSlashesIn(s: String): String {
        return if (TextUtils.isEmpty(s)) {
            ""
        } else s.replace(FORWARD_SLASH_REGEX.toRegex(), "")
    }

    fun getThrowableName(throwable: Throwable): String {
        return throwable.javaClass.name
    }

    fun <T> valid(info: T?, whiteListInfo: T?): Boolean {
        if (info == null || info == "") {
            return false
        }
        return if (whiteListInfo == null || whiteListInfo == "") {
            true
        } else whiteListInfo == info
    }

    fun <T> valid(info: T?, whiteListInfo: List<T>?): Boolean {
        if (info == null || info == "") {
            return false
        }
        return if (whiteListInfo == null || whiteListInfo.isEmpty()) {
            true
        } else whiteListInfo.contains(info)
    }

    fun <T> valid(info: Map<String, T>?, whiteListInfo: Map<String, T>?): Boolean {
        if (info == null || info.isEmpty()) {
            return true
        }
        if (whiteListInfo == null || whiteListInfo.isEmpty()) {
            return true
        }
        for (whiteKey in whiteListInfo.keys) {
            val whiteValue = whiteListInfo[whiteKey] ?: continue
            val infoValue = info[whiteKey] ?: continue
            if (whiteValue == infoValue) {
                return true
            }
        }
        return false
    }
}
