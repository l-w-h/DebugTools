package com.lwh.debugtools.db.table.base

import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debug.delegate.MapDelegate
import java.io.Serializable

/**
 * @author lwh
 * @Date 2019/8/29 22:25
 * @description BaseTable
 */
abstract class BaseTable(map: MutableMap<String, Any?>) : Serializable, BaseModelImpl() {
    var _id: Int by MapDelegate<Int>(map)
    var expand: String? by MapDelegate<String?>(map)


    constructor() : this(LinkedHashMap()) {

    }
}