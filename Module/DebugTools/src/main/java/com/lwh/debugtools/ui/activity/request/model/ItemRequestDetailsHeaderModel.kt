package com.lwh.debugtools.ui.activity.request.model

import com.lwh.debugtools.base.item.BaseItemModel
import com.lwh.debugtools.base.utils.unit.UnitUtils
import com.lwh.debug.delegate.MapDelegate
import com.lwh.debugtools.db.table.RequestTable

/**
 * @author lwh
 * @Date 2019/10/21 11:54
 * @description ItemRequestDetailsHeaderModel
 */
data class ItemRequestDetailsHeaderModel(val request: RequestTable) : BaseItemModel() {
    var url: String by MapDelegate(request.map)
    var method: String? by MapDelegate(request.map)
    var mediaType: String? by MapDelegate(request.map)
    var contentLength: Long? by MapDelegate(request.map)
    var decryptContentLength: Long? by MapDelegate(request.map)
    var code: Long? by MapDelegate(request.map)
    var sentRequestAtMillis: Long? by MapDelegate(request.map)
    var receivedResponseAtMillis: Long? by MapDelegate(request.map)


    fun getTimeConsumingStr(): String {
        if (receivedResponseAtMillis != null && sentRequestAtMillis != null) {
            return UnitUtils.timeUnit(receivedResponseAtMillis!! - sentRequestAtMillis!!)
        }
        return "--"
    }

    fun getCodeStr(): String {
        return "$code"
    }

    fun getContentLengthStr(): String {
        return UnitUtils.fileSize(if (contentLength != null) contentLength!!.toDouble() else 0.toDouble())
    }

    fun getDecryptContentLengthStr(): String {
        return UnitUtils.fileSize(if (decryptContentLength != null) decryptContentLength!!.toDouble() else 0.toDouble())
    }

}