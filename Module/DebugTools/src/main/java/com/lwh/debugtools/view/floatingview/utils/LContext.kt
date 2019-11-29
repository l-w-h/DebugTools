package com.lwh.debugtools.view.floatingview.utils

import android.app.Application

/**
 * @author lwh
 * @Date 2019/11/21 17:58
 * @description 获取Application对象
 */
object LContext {

    private val INSTANCE: Application

    init {
        var app: Application? = null
        try {
            app = Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null) as Application
            if (app == null) {
                throw IllegalStateException("Static initialization of Applications must be on main thread.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                app =
                    Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null) as Application
            } catch (ex: Exception) {
                e.printStackTrace()
            }

        } finally {
            INSTANCE = app!!
        }
    }

    fun get(): Application {
        return INSTANCE
    }
}
