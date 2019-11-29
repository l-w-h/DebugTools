package com.lwh.debugtoolsdemo.api

import retrofit2.http.GET
import retrofit2.http.HeaderMap
import rx.Observable

/**
 * @author lwh
 * @Date 2019/8/26 20:34
 * @description ServiceApi
 */
interface ServiceApi {


    @GET("api/config")
    fun homepageHead(): Observable<Any>
}