package com.lwh.debugtools.view.floatingview.utils

import android.content.Context

/**
 * @author lwh
 * @Date 2019/11/21 17:58
 * @description 系统工具类
 */
object SystemUtils {

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getScreenWidth(context: Context): Int {
        var screenWith = -1
        try {
            screenWith = context.resources.displayMetrics.widthPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return screenWith
    }

    fun getScreenHeight(context: Context): Int {
        var screenHeight = -1
        try {
            screenHeight = context.resources.displayMetrics.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return screenHeight
    }

}
