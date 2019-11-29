package com.lwh.debugtools.ui.activity.log.model

import com.lwh.debugtools.base.item.BaseItemModel
import com.lwh.debug.delegate.MapDelegate
import com.lwh.debugtools.db.table.LogTable

/**
 * @author lwh
 * @Date 2019/10/25 11:42
 * @description ItemLogDetailsHeaderModel
 */
class ItemLogDetailsHeaderModel(logTable: LogTable): BaseItemModel(){
    var type : String by MapDelegate<String>(logTable.map)
    var location : String by MapDelegate<String>(logTable.map)
    var time : Long by MapDelegate<Long>(logTable.map)
    var content : String? by MapDelegate<String?>(logTable.map)
    var tag : String? by MapDelegate<String?>(logTable.map)
    var logStack : String? by MapDelegate<String?>(logTable.map)

}