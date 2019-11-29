package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTable
import com.lwh.debug.delegate.MapDelegate

/**
 * @author lwh
 * @Date 2019/8/29 22:24
 * @description 错误异常表
 */
data class ErrorTable(val map: MutableMap<String, Any?>) : BaseTable(map) {
    var buildVersion: String? by MapDelegate<String?>(map)
    var buildDate: String? by MapDelegate<String?>(map)
    var device: String? by MapDelegate<String?>(map)
    var throwable: String? by MapDelegate<String?>(map)
    var stack: String? by MapDelegate<String?>(map)
    var userActions: String? by MapDelegate<String?>(map)
    var crashDate: Long by MapDelegate<Long>(map)
    var state: Long? by MapDelegate<Long?>(map)

    constructor() : this(LinkedHashMap()) {

    }

}