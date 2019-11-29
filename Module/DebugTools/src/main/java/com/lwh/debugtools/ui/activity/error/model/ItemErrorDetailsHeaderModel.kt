package com.lwh.debugtools.ui.activity.error.model

import com.lwh.debugtools.base.item.BaseItemModel
import com.lwh.debug.delegate.MapDelegate
import com.lwh.debugtools.db.table.ErrorTable

/**
 * @author lwh
 * @Date 2019/11/22 10:22
 * @description
 */
class ItemErrorDetailsHeaderModel(errorTable: ErrorTable) : BaseItemModel(){
    var buildVersion: String? by MapDelegate<String?>(errorTable.map)
    var buildDate: String? by MapDelegate<String?>(errorTable.map)
    var device: String? by MapDelegate<String?>(errorTable.map)
    var throwable: String? by MapDelegate<String?>(errorTable.map)
    var stack: String? by MapDelegate<String?>(errorTable.map)
    var userActions: String? by MapDelegate<String?>(errorTable.map)
    var crashDate: Long by MapDelegate<Long>(errorTable.map)
    var state: Long? by MapDelegate<Long?>(errorTable.map)

}