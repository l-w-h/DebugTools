package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTable
import com.lwh.debug.delegate.MapDelegate

/**
 * @author lwh
 * @Date 2019/8/25 15:49
 * @description RequestTable
 */
data class RequestTable(val map:MutableMap<String,Any?>) : BaseTable(map){
    var url : String by MapDelegate<String>(map)
    var host : String by MapDelegate<String>(map)
    var path : String? by MapDelegate<String?>(map)
    var query : String? by MapDelegate<String?>(map)
    var method : String? by MapDelegate<String?>(map)
    var requestHeader : String? by MapDelegate<String?>(map)
    var responseHeader : String? by MapDelegate<String?>(map)
    var requestBody : String? by MapDelegate<String?>(map)
    var decryptRequestBody : String? by MapDelegate<String?>(map)
    var responseBody : String? by MapDelegate<String?>(map)
    var decryptResponseBody : String? by MapDelegate<String?>(map)
    var mediaType : String? by MapDelegate<String?>(map)
    var contentLength : Long? by MapDelegate<Long?>(map)
    var decryptContentLength : Long? by MapDelegate<Long?>(map)
    var code : Long? by MapDelegate<Long?>(map)
    var sentRequestAtMillis : Long? by MapDelegate<Long?>(map)
    var receivedResponseAtMillis : Long? by MapDelegate<Long?>(map)
    var errorMessage : String? by MapDelegate<String?>(map)

    constructor():this(LinkedHashMap()){

    }
}


