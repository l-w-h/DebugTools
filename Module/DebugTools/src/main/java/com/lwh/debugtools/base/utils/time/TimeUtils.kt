package com.lwh.debugtools.base.utils.time

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author lwh
 * @Date 2019/8/29 15:53
 * @description TimeUtils
 */
object TimeUtils {
    fun getDate(pattern: String, millis: Long): String {
        val format = SimpleDateFormat(pattern)
        return format.format(Date(millis))
    }
}
