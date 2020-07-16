package com.lwh.debugtoolsdemo.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

/**
 * @author lwh
 * @Date 2019/8/26 20:34
 * @description ServiceApi
 */
interface ServiceApi {


    @GET("xxx/xxx/xxx/xxx")
    fun get(): Observable<Map<String,Any>>
    @POST("xxx/xxx/xxx")
    fun save(@Body map:Map<String,String>): Observable<Map<String,Any>>
}