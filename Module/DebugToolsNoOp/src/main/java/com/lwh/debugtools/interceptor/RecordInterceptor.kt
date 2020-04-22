package com.lwh.debugtools.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

/**
 * @author lwh
 * @Date 2019/8/26 11:51
 * @description 拦截记录网络请求
 */
class RecordInterceptor : Interceptor {
    internal val context: Context
    private val callback:OnDecryptCallback?

    constructor(context: Context):this(context,null)

    constructor(context: Context,callback:OnDecryptCallback?) {
        this.context = context
        this.callback = callback
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
    /**
     * 解密回调
     */
    public interface OnDecryptCallback{
        /**
         * 请求body解密
         */
        fun onRequestBodyDecrypt(body:String):String
        /**
         * 结果body解密
         */
        fun onResponseBodyDecrypt(body:String?):String?
    }
}
