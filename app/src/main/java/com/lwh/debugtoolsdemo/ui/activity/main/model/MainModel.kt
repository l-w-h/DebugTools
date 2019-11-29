package com.lwh.debugtoolsdemo.ui.activity.main.model

import com.lwh.debugtoolsdemo.api.ServiceApi
import com.lwh.debugtoolsdemo.api.ServiceUtils
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
            .homepageHead()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Any> {
                override fun onError(e: Throwable?) {
                    fail(e)
                }

                override fun onNext(t: Any?) {
                    success(t)
                }

                override fun onCompleted() {

                }

            })
    }


}