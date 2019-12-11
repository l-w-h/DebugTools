package com.lwh.debugtoolsdemo

import android.app.Application
import android.widget.Toast
import com.lwh.debugtools.DebugTools
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener
import com.lwh.debugtoolsdemo.ui.activity.main.MainActivity

/**
 * @author lwh
 * @Date 2019/10/19 14:22
 * @description App
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DebugTools.getInstance(this).init(true).initCrash(
            MainActivity::class.java
        ).startWhitecrashIntercept(onCrashListener = object :OnCrashListener{
            override fun onCrash(throwable: Throwable) {
                Toast.makeText(this@App,"""
                    |捕获到崩溃信息：
                    |${throwable.javaClass.simpleName}
                    |${throwable.message}""".trimMargin(),Toast.LENGTH_SHORT).show()
            }

        }).addIgnoreUrl("app/system/downApk")
    }
}