package com.lwh.debugtools.utils.whitecrash

import com.lwh.debugtools.utils.whitecrash.info.CrashInfo
import com.lwh.debugtools.utils.whitecrash.listener.OnCrashListener

/**
 * Created by Yunpeng Li on 2019-09-03.
 */
class WhiteCrash private constructor() {

    fun catchCrash(crashInfo: CrashInfo): WhiteCrash {
        CrashWhiteList.addWhiteCrash(crashInfo)
        return this
    }

    fun setCustomInfo(customInfo: Map<String, String>): WhiteCrash {
        CrashWhiteList.customInfo = customInfo
        return this
    }

    fun setInterceptAll(interceptAll: Boolean): WhiteCrash {
        CrashCatcher.interceptAll = interceptAll
        return this
    }

    fun setOnCrashListener(onCrashListener: OnCrashListener?): WhiteCrash {
        CrashCatcher.onCrashListener = onCrashListener
        return this
    }

    fun start(): WhiteCrash {
        CrashCatcher.start()
        return this
    }

    fun stop(): WhiteCrash {
        CrashCatcher.stop()
        return this
    }

    fun matchAll(): WhiteCrash {
        CrashCatcher.MATCH_ALL = true
        return this
    }

    companion object {

        @Volatile
        private var mInstance: WhiteCrash? = null

        fun get(): WhiteCrash? {
            if (mInstance == null) {
                synchronized(WhiteCrash::class.java) {
                    if (mInstance == null) {
                        mInstance = WhiteCrash()
                    }
                }
            }
            return mInstance
        }

        val deviceName: String
            get() = CrashInfoUtils.deviceName

        val buildVersion: String
            get() = CrashInfoUtils.buildVersion
    }
}
