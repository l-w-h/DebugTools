package com.lwh.debugtoolsdemo.ui.activity.main.presenter

import com.lwh.debugtoolsdemo.ui.activity.main.model.MainModel

/**
 * @author lwh
 * @Date 2019/10/19 11:35
 * @description MainPresenter
 */
class MainPresenter {

    private val mainModel = MainModel()

    fun request(success: (Any?) -> Unit, fail: (Throwable?) -> Unit) {
        mainModel.request(success, fail)
    }

}