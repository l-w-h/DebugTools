package com.lwh.debugtools.interceptor

import android.content.Context
import android.text.TextUtils
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.RequestTable
import okhttp3.*
import okio.Buffer
import java.io.IOException

/**
 * @author lwh
 * @Date 2019/8/26 11:51
 * @description 拦截记录网络请求
 */
class RecordInterceptor : Interceptor {
    internal val context: Context
    private val callback: OnInterceptorCallback

    constructor(context: Context) : this(context, OnInterceptorCallbackImpl())

    constructor(context: Context, callback: OnInterceptorCallback) {
        this.context = context
        this.callback = callback
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        var request = chain.request()
        val requestBuilder = request.newBuilder()
        val url = request.url().url().toString()

        //忽略拦截url
        if (callback.isIgnoreUrl(url)) {
            return chain.proceed(request)
        }

        val startMillis = System.currentTimeMillis()
        val requestTable = RequestTable()
        //是否解析接口
        val isAnalyzeRequestBody = callback.isAnalyzeRequestBody(url)
        //是否加密接口
        val isEncryptRequestBody = callback.isEncryptRequestBody(url)
        val requestBodyStr =
            if (isAnalyzeRequestBody || isEncryptRequestBody) bodyToString(request.body()) else "不支持查看当前内容"
        //加密请求body
        val encryptRequestBodyStr = if (isEncryptRequestBody) callback.onRequestBodyEncrypt(
            url,
            requestBodyStr
        ) else requestBodyStr
        val method = request.method()
        val requestHeaders = request.headers()
        requestTable.method = method
        requestTable.host = request.url().host()
        requestTable.path = request.url().encodedPath()
        requestTable.query = request.url().query()
        requestTable.url = url
        requestTable.requestHeader = requestHeaders?.toString()
        requestTable.requestBody = encryptRequestBodyStr
        requestTable.decryptRequestBody = if (isEncryptRequestBody) requestBodyStr else ""
        requestTable.code = 0
        requestTable.sentRequestAtMillis = startMillis
        requestTable.receivedResponseAtMillis = 0

        //替换body内容
        if (TextUtils.equals(
                request.method(),
                "POST"
            ) && (isAnalyzeRequestBody || isEncryptRequestBody)
        ) {
            val newBody: RequestBody = RequestBody.create(
                MediaType.parse("application/json"),
                encryptRequestBodyStr
            )
            requestBuilder.post(newBody)
            requestBuilder.removeHeader("Content-Length")
            requestBuilder.addHeader(
                "Content-Length",
                encryptRequestBodyStr?.toByteArray()?.size?.toString() ?: "-1"
            )
            request = requestBuilder.build()
        }

        try {
            val response = chain.proceed(request)
            var responseBody = response.body()
            val endMillis = System.currentTimeMillis()
            //是否解析接口
            val isAnalyzeResponseBody = callback.isAnalyzeResponseBody(url)
            //是否解密接口
            val isDecryptResponseBody = callback.isDecryptResponseBody(url)
            val responseBodyStr =
                if (isAnalyzeResponseBody || isDecryptResponseBody) responseBody?.string() else "不支持查看当前内容"
            var decryptResponseBodyStr = if (isDecryptResponseBody) callback.onResponseBodyDecrypt(
                url,
                responseBodyStr
            ) else ""
            val contentLength = responseBody?.contentLength()
            val mediaType = responseBody?.contentType()
            val responseHeaders = response.headers()
            val code = response.code()
            val sentRequestAtMillis =
                if (response.sentRequestAtMillis() <= 0) startMillis else response.sentRequestAtMillis()
            val receivedResponseAtMillis =
                if (response.receivedResponseAtMillis() <= 0) endMillis else response.receivedResponseAtMillis()
            val handshake = response.handshake()
            val message = response.message()
            val protocol = response.protocol()

            requestTable.code = code.toLong()
            requestTable.responseBody = responseBodyStr
            requestTable.decryptResponseBody = decryptResponseBodyStr
            requestTable.contentLength =
                if (contentLength == -1L) (requestTable.responseBody?.toByteArray()?.size
                    ?: 0) * 1L else contentLength
            requestTable.decryptContentLength =
                requestTable.decryptResponseBody?.toByteArray()?.size?.toLong() ?: 0L
            requestTable.responseHeader = responseHeaders?.toString()
            requestTable.mediaType = mediaType?.toString()
            requestTable.sentRequestAtMillis = sentRequestAtMillis
            requestTable.receivedResponseAtMillis = receivedResponseAtMillis
            DatabaseUtils.putRequest(requestTable)

            if (isAnalyzeResponseBody || isDecryptResponseBody) {
                responseBody = ResponseBody.create(mediaType, responseBodyStr)
                return response.newBuilder().body(responseBody).build()
            }
            return response.newBuilder().build()
        } catch (e: Exception) {
            val stringBuilder = StringBuilder(e.javaClass.toString())
            e.stackTrace.forEach {
                stringBuilder.append("\n").append(it.toString())
            }
            requestTable.errorMessage = stringBuilder.toString()
            requestTable.sentRequestAtMillis =
                if (requestTable.sentRequestAtMillis != null) requestTable.sentRequestAtMillis else startMillis
            requestTable.receivedResponseAtMillis =
                if (requestTable.receivedResponseAtMillis!! != 0L) requestTable.receivedResponseAtMillis else System.currentTimeMillis()
            DatabaseUtils.putRequest(requestTable)
            throw e
        } finally {

//            Log.i("TAG", "recordInterceptor finally")
        }
    }


    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        var subtype = mediaType.subtype()
        if (subtype != null) {
            subtype = subtype.toLowerCase()
            if (subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("html")
            ) //
                return true
        }
        return false
    }

    /**
     * RequestBody 转string
     */
    private fun bodyToString(request: RequestBody?): String {
        return try {
            val buffer = Buffer()
            if (request != null) {
                request.writeTo(buffer)
            } else {
                return ""
            }
            buffer.readUtf8()
        } catch (e: IOException) {
            //"did not work"
            ""
        } catch (e: OutOfMemoryError) {
            e.localizedMessage
            ""
        } catch (e: Exception) {
            e.localizedMessage
            ""
        }

    }

    /**
     * 解密回调
     */
    interface OnInterceptorCallback {
        /**
         * 是否解析请求body
         */
        fun isAnalyzeRequestBody(url: String): Boolean

        /**
         * 是否解析返回body
         */
        fun isAnalyzeResponseBody(url: String): Boolean

        /**
         * 是否加密请求body
         */
        fun isEncryptRequestBody(url: String): Boolean

        /**
         * 是否解密返回body
         */
        fun isDecryptResponseBody(url: String): Boolean

        /**
         * 请求body加密
         */
        fun onRequestBodyEncrypt(url: String, body: String?): String?

        /**
         * 返回body解密
         */
        fun onResponseBodyDecrypt(url: String, body: String?): String?

        /**
         * 忽略url
         */
        fun isIgnoreUrl(url: String): Boolean
    }

    open class OnInterceptorCallbackImpl : OnInterceptorCallback {
        override fun isAnalyzeRequestBody(url: String): Boolean = true

        override fun isAnalyzeResponseBody(url: String): Boolean = true

        override fun isEncryptRequestBody(url: String): Boolean = false

        override fun isDecryptResponseBody(url: String): Boolean = false

        override fun onRequestBodyEncrypt(url: String, body: String?): String? {
            return body
        }

        override fun onResponseBodyDecrypt(url: String, body: String?): String? {
            return ""
        }

        override fun isIgnoreUrl(url: String): Boolean = false

    }

}
