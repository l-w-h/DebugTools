package com.lwh.debugtools.interceptor

import android.content.Context
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

/**
 * @author lwh
 * @Date 2019/8/26 11:51
 * @description 拦截记录网络请求
 */
class RecordInterceptor : Interceptor {
    internal val context: Context
    private val callback: OnInterceptorCallback
    private val UTF8 = Charset.forName("UTF-8")

    constructor(context: Context) : this(context, OnInterceptorCallbackImpl())

    constructor(context: Context, callback: OnInterceptorCallback) {
        this.context = context
        this.callback = callback
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBuilder = request.newBuilder()
        val url = request.url().url().toString()
        //忽略拦截url
        if (callback.isIgnoreUrl(url)) {
            return chain.proceed(request)
        }
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

        return chain.proceed(request)
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
