package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTable
import com.lwh.debug.delegate.MapDelegate

/**
 * @author lwh
 * @Date 2019/8/27 22:47
 * @description LogTable
 */
data class LogTable (val map:MutableMap<String,Any?>) : BaseTable(map){
    var type : String by MapDelegate<String>(map)
    var location : String by MapDelegate<String>(map)
    var time : Long by MapDelegate<Long>(map)
    var content : String? by MapDelegate<String?>(map)
    var tag : String? by MapDelegate<String?>(map)
    var logStack : String? by MapDelegate<String?>(map)

    constructor():this(LinkedHashMap()){

    }

}