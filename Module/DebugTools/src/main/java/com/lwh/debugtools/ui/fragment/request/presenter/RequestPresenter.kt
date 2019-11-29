package com.lwh.debugtools.ui.fragment.request.presenter

import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.RequestTable
import com.lwh.debugtools.ui.fragment.request.model.RequestModel

/**
 * @author lwh
 * @Date 2019/10/19 16:21
 * @description RequestPresenter
 */
class RequestPresenter : BasePresenterImpl() {

    private val model: RequestModel = RequestModel()

    fun loadData(page: Int, pageSize: Int, success: (PaginationBean<RequestTable>) -> Unit) {
        model.loadData(page, pageSize, success)
    }
}