package com.lwh.debugtools.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author lwh
 * @Date 2019/8/26 11:51
 * @description 拦截记录网络请求
 */
class RecordInterceptor(internal val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
