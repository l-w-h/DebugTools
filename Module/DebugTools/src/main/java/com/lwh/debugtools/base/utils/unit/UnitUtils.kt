package com.lwh.debugtools.base.utils.unit

import java.math.BigDecimal

/**
 * @author lwh
 * @Date 2019/8/25 15:27
 * @description 单位工具
 */
object UnitUtils {

    /** 文件大小格式化单位  */
    fun fileSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "B"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return "${result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return "${result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return "${result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}GB"
        }
        val result4 = BigDecimal(teraBytes)
        return "${result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}TB"
    }

    /** 时间单位  */
    fun timeUnit(ms: Long): String {
        val millis = (ms / 1000f).toDouble()

        if (millis < 1) {
            return  "${BigDecimal(millis).setScale(2,BigDecimal.ROUND_HALF_UP)}ms"
        }
        val second = millis / 60f
        if (second < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(millis))
            return  "${result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}s"
        }
        val minute = second / 60f
        if (minute < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(second))
            return  "${result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}m"
        }
        val hour = minute / 60f
        if (hour < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(minute))
            return  "${result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}H"
        }
        val result4 = BigDecimal(hour)
        return  "${result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}d"
    }

}
