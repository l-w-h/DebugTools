package com.lwh.debugtools.interceptor

import android.content.Context
import android.util.Log
import com.lwh.debugtools.DebugTools
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.RequestTable
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * @author lwh
 * @Date 2019/8/26 11:51
 * @description 拦截记录网络请求
 */
class RecordInterceptor : Interceptor {
    internal val context: Context
    private val callback:OnDecryptCallback?
    private val UTF8 = Charset.forName("UTF-8")

    constructor(context: Context):this(context,null)

    constructor(context: Context,callback:OnDecryptCallback?) {
        this.context = context
        this.callback = callback
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val url = request.url().url().toString()

        //忽略拦截url
        if (DebugTools.ignoreUrls.indexOf(url) != -1) {
            return chain.proceed(request)
        }
        DebugTools.ignoreUrls.forEach { ignoreUrl ->
            if (url.contains(ignoreUrl)) {
                return chain.proceed(request)
            }
        }

        val startMillis = System.currentTimeMillis()
        val requestTable = RequestTable()
        val requestBodyStr = bodyToString(request)
        val decryptRequestBodyStr = callback?.onRequestBodyDecrypt(url,requestBodyStr)
        val method = request.method()
        val requestHeaders = request.headers()
        requestTable.method = method
        requestTable.host = request.url().host()
        requestTable.path = request.url().encodedPath()
        requestTable.query = request.url().query()
        requestTable.url = url
        requestTable.requestHeader = requestHeaders?.toString()
        requestTable.requestBody = requestBodyStr
        requestTable.decryptRequestBody = decryptRequestBodyStr
        requestTable.code = 0
        requestTable.sentRequestAtMillis = startMillis
        requestTable.receivedResponseAtMillis = 0


        try {
            val response = chain.proceed(request)
            var responseBody = response.body()
            val endMillis = System.currentTimeMillis()
            val responseBodyStr = responseBody?.string()
            var decryptResponseBodyStr = callback?.onResponseBodyDecrypt(url,responseBodyStr)
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
            requestTable.contentLength =
                if (contentLength == -1L) (responseBodyStr?.toByteArray()?.size
                    ?: 0) * 1L else contentLength
            requestTable.decryptContentLength = decryptResponseBodyStr?.toByteArray()?.size?.toLong() ?: 0L
            requestTable.responseHeader = responseHeaders?.toString()
            requestTable.mediaType = mediaType?.toString()
            requestTable.responseBody = responseBodyStr
            requestTable.decryptResponseBody = decryptResponseBodyStr
            requestTable.sentRequestAtMillis = sentRequestAtMillis
            requestTable.receivedResponseAtMillis = receivedResponseAtMillis
            DatabaseUtils.putRequest(requestTable)

            responseBody = ResponseBody.create(mediaType, responseBodyStr)
            return response.newBuilder().body(responseBody).build()
//            return responseBuilder.build()
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
     * RequestBody 转string
     */
    private fun bodyToString(request: Request): String {
        try {
            val copy = request?.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            var charset: Charset? = UTF8
            val contentType = copy.body()!!.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            return URLDecoder.decode(buffer.readString(charset), UTF8.name())
//            val buffer = Buffer()
//            if (request != null) {
//                request.writeTo(buffer)
//            } else {
//                return ""
//            }
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        } catch (e: NullPointerException) {
            return ""
        } catch (e: Exception) {
            return "${(e.javaClass as Class).name} ${e.localizedMessage}"
        }

    }

    /**
     * 解密回调
     */
    public interface OnDecryptCallback{
        /**
         * 请求body解密
         */
        fun onRequestBodyDecrypt(url:String,body:String):String?
        /**
         * 结果body解密
         */
        fun onResponseBodyDecrypt(url:String,body:String?):String?
    }

}
