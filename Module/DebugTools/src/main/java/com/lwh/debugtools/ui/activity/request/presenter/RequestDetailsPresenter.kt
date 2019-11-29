package com.lwh.debugtools.ui.activity.request.presenter

import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.ui.activity.request.model.RequestDetailsModel

/**
 * @author lwh
 * @Date 2019/10/21 9:43
 * @description RequestDetailsPresenter
 */
class RequestDetailsPresenter : BasePresenterImpl(){
    val model = RequestDetailsModel()

    fun loadData(success: (ArrayList<BaseGroupItem>) -> Unit, id: Int) {
        model.loadData(success,id)
    }
}