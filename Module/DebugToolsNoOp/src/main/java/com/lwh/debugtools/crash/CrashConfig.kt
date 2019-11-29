package com.lwh.debugtools.crash

import androidx.annotation.IntDef
import java.io.Serializable


const val BACKGROUND_MODE_SILENT = 0
const val BACKGROUND_MODE_SHOW_CUSTOM = 1
const val BACKGROUND_MODE_CRASH = 2

/**
 * @author lwh
 * @Date 2019/8/29 21:36
 * @description CrashConfig
 */
class CrashConfig : Serializable {


    @IntDef(BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class BackgroundMode


    /**
     * Interface to be called when events occur, so they can be reported
     * by the app as, for example, Google Analytics events.
     */
    interface EventListener : Serializable {
        fun onLaunchErrorActivity()

        fun onRestartAppFromErrorActivity()

        fun onCloseAppFromErrorActivity()
    }

}