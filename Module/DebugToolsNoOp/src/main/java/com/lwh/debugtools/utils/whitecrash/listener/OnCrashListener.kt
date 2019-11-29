package com.lwh.debugtools.utils.whitecrash.listener

/**
 * @author lwh
 * @Date 2019/11/22 16:54
 * @description
 */
interface OnCrashListener {

    fun onCrash(throwable: Throwable)

}