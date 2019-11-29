package com.lwh.debugtools.ui.activity.error.presenter

import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.ui.activity.error.model.ErrorDetailsModel

/**
 * @author lwh
 * @Date 2019/11/22 10:21
 * @description
 */
class ErrorDetailsPresenter : BasePresenterImpl() {

    private val model = ErrorDetailsModel()
    fun loadData(id: Int, success: (ArrayList<BaseGroupItem>) -> Unit) {
        model.loadData(id, success)
    }
}