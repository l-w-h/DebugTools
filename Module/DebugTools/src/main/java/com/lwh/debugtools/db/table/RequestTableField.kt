package com.lwh.debugtools.db.table

import com.lwh.debugtools.db.table.base.BaseTableField

/**
 * @author lwh
 * @Date 2019/8/25 17:16
 * @description RequestTableField
 */
object RequestTableField : BaseTableField(){
    /** 表名 */
    const val TABLE = "RequestTable"

    /** 请求路径 */
    const val URL = "url"

    /** 请求路径host */
    const val HOST = "host"

    /** 请求路径path */
    const val PATH = "path"

    /** GET参数 */
    const val QUERY = "query"

    /** 请求方法 */
    const val METHOD = "method"

    /** 请求头 */
    const val REQUEST_HEADER = "requestHeader"

    /** 响应头 */
    const val RESPONSE_HEADER = "responseHeader"

    /** 请求body */
    const val REQUEST_BODY = "requestBody"

    /** 解密后的请求body */
    const val DECRYPT_REQUEST_BODY = "decryptRequestBody"

    /** 响应body */
    const val RESPONSE_BODY = "responseBody"

    /** 解密后的响应body */
    const val DECRYPT_RESPONSE_BODY = "decryptResponseBody"

    /** 数据类型 */
    const val MEDIA_TYPE = "mediaType"

    /** 响应body大小 */
    const val CONTENT_LENGTH = "contentLength"

    /** 解密后的响应body大小 */
    const val DECRYPT_CONTENT_LENGTH = "decryptContentLength"

    /** 请求码 200 404 ... */
    const val CODE = "code"

    /** 请求时间 */
    const val SENT_REQUEST_AT_MILLIS = "sentRequestAtMillis"

    /** 响应时间 */
    const val RECEIVED_RESPONSE_AT_MILLIS = "receivedResponseAtMillis"

    /** 错误信息 */
    const val ERROR_MESSAGE = "errorMessage"
}