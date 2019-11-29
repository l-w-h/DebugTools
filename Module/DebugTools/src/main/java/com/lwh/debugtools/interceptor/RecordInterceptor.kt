package com.lwh.debugtools.interceptor

import android.content.Context
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
class RecordInterceptor(internal val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val startMillis = System.currentTimeMillis()
        val requestTable = RequestTable()
        val requestBodyStr = bodyToString(request.body())
        val method = request.method()
        val requestHeaders = request.headers()
        requestTable.method = method
        requestTable.host = request.url().host()
        requestTable.path = request.url().encodedPath()
        requestTable.query = request.url().query()
        requestTable.url = request.url().url().toString()
        requestTable.requestHeader = requestHeaders?.toString()
        requestTable.requestBody = requestBodyStr
        requestTable.code = 0
        requestTable.sentRequestAtMillis = startMillis
        requestTable.receivedResponseAtMillis = 0
        try {
            val response = chain.proceed(request)
            val endMillis = System.currentTimeMillis()
            val responseBodyStr = response.body()!!.string()
            val contentLength = response.body()!!.contentLength()
            val mediaType = response.body()!!.contentType()
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
                if (contentLength == -1L) responseBodyStr.toByteArray().size * 1L else contentLength
            requestTable.responseHeader = responseHeaders?.toString()
            requestTable.mediaType = mediaType?.toString()
            requestTable.responseBody = responseBodyStr
            requestTable.sentRequestAtMillis = sentRequestAtMillis
            requestTable.receivedResponseAtMillis = receivedResponseAtMillis
            DatabaseUtils.putRequest(requestTable)
            val responseBuilder = getResponse(
                responseBodyStr,
                mediaType,
                responseHeaders,
                code,
                sentRequestAtMillis,
                receivedResponseAtMillis,
                handshake,
                message,
                protocol,
                request
            )
            return responseBuilder.build()
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
        }
    }

    /**
     * RequestBody 转string
     */
    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null) {
                request.writeTo(buffer)
            } else {
                return ""
            }
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        } catch (e: OutOfMemoryError) {
            return "${e.localizedMessage}"
        } catch (e: Exception) {
            return "${e.localizedMessage}"
        }

    }

    private fun getResponse(
        responseBodyStr: String,
        mediaType: MediaType?,
        headers: Headers?,
        code: Int,
        sentRequestAtMillis: Long,
        receivedResponseAtMillis: Long,
        handshake: Handshake?,
        message: String,
        protocol: Protocol,
        request: Request
    ): Response.Builder {
        val responseBody = ResponseBody.create(mediaType, responseBodyStr)
        val responseBuilder = Response.Builder()
        responseBuilder.headers(headers!!)
        responseBuilder.code(code)
        responseBuilder.sentRequestAtMillis(sentRequestAtMillis)
        responseBuilder.receivedResponseAtMillis(receivedResponseAtMillis)
        responseBuilder.handshake(handshake)
        responseBuilder.message(message)
        responseBuilder.protocol(protocol)
        responseBuilder.request(request)
        responseBuilder.body(responseBody)
        return responseBuilder
    }
}
