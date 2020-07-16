package com.lwh.debugtoolsdemo.ui.activity.main.model

import com.lwh.debugtoolsdemo.api.ServiceApi
import com.lwh.debugtoolsdemo.api.ServiceUtils
import org.json.JSONObject
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @author lwh
 * @Date 2019/10/19 11:35
 * @description MainModel
 */
class MainModel {

    fun request(success: (Any?) -> Unit, fail: (Throwable?) -> Unit) {
        ServiceUtils.getApi(ServiceApi::class.java)
            .get()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Map<String,Any>> {
                override fun onError(e: Throwable?) {
                    fail(e)
                }

                override fun onNext(t: Map<String,Any>) {
                    val data = t.get("data")
                    success(data)
                    save(data.toString(),success)
                }

                override fun onCompleted() {

                }

            })
    }


    fun save(data:String,success: (Any?) -> Unit){
        ServiceUtils.getApi(ServiceApi::class.java)
            .save(mapOf("xxx" to data, "xxx" to data))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Map<String,Any>> {
                override fun onError(e: Throwable?) {

                }

                override fun onNext(t: Map<String,Any>) {
                    val data = t.get("data")
                    success(data)

                }

                override fun onCompleted() {

                }

            })
    }
}