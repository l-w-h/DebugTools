package com.lwh.debugtools.view.statusview

import com.lwh.debugtools.view.floatingview.utils.LContext
import java.lang.reflect.Field

/**
 * @author lwh
 * @Date 2019/11/25 10:56
 * @description
 */
object StatusUtils {
    /**
     * 获取状态栏高度
     */
    fun getStatusBarSize(): Int {
        val c: Class<*>
        val obj: Any
        val field: Field
        var x = 0
        var statusBarSize = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field.get(obj).toString())
            statusBarSize = LContext.get().resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarSize
    }
}