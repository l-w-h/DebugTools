package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTableField

/**
 * @author lwh
 * @Date 2019/8/27 22:51
 * @description LogTableField
 */
object LogTableField : BaseTableField() {

    /** 表名 */
    const val TABLE = "LogTable"

    /**
     * 打印类型
     */
    const val TYPE = "type"

    /**
     * 打印位置
     */
    const val LOCATION = "location"

    /**
     * 打印时间
     */
    const val TIME = "time"

    /**
     * 打印内容
     */
    const val CONTENT = "content"

    /**
     * 打印Tag
     */
    const val TAG = "tag"

    /**
     * log堆栈信息
     */
    const val LOG_STACK = "logStack"
}