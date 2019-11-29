package com.lwh.debugtools.ui.activity.log.presenter

import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.ui.activity.log.model.LogDetailsModel

/**
 * @author lwh
 * @Date 2019/10/25 11:49
 * @description LogDetailsPresenter
 */
class LogDetailsPresenter : BasePresenterImpl() {

    private val model = LogDetailsModel()
    fun loadData(id: Int, success: (ArrayList<BaseGroupItem>) -> Unit) {
        model.loadData(id,success)
    }
}