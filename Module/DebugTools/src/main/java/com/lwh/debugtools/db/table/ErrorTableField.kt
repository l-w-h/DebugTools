package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTableField

/**
 * @author lwh
 * @Date 2019/8/29 22:24
 * @description 错误异常表
 */
object ErrorTableField : BaseTableField() {
    /** 表名 */
    const val TABLE = "ErrorTable"

    /**
     * app版本
     */
    const val BUILD_VERSION = "buildVersion"

    /**
     * apk最后更新时间
     */
    const val BUILD_DATE = "buildDate"

    /**
     * 设备
     */
    const val DEVICE = "device"

    /**
     * 抛出异常类型
     */
    const val THROWABLE = "throwable"

    /**
     * 堆栈
     */
    const val STACK = "stack"

    /**
     * 用户操作
     */
    const val USER_ACTIONS = "userActions"

    /**
     * 崩溃日期
     */
    const val CRASH_DATE = "crashDate"

    /**
     * 状态 0：未拦截 1：拦截
     */
    const val STATE = "state"

}