package com.lwh.debugtools.ui.fragment.error.presenter

import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.ErrorTable
import com.lwh.debugtools.ui.fragment.error.model.ErrorModel

/**
 * @author lwh
 * @Date 2019/11/22 10:15
 * @description
 */
class ErrorPresenter : BasePresenterImpl() {

    val model = ErrorModel()

    fun loadData(page: Int, pageSize: Int, success: (PaginationBean<ErrorTable>) -> Unit) {
        model.loadData(page, pageSize, success)
    }
}