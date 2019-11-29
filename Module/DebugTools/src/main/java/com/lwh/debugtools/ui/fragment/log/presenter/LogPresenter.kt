package com.lwh.debugtools.ui.fragment.log.presenter

import com.lwh.debugtools.base.mvp.BasePresenterImpl
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.LogTable
import com.lwh.debugtools.ui.fragment.log.model.LogModel

/**
 * @author lwh
 * @Date 2019/10/25 11:09
 * @description LogPresenter
 */
class LogPresenter : BasePresenterImpl(){

    val model = LogModel()

    fun loadData(page:Int,pageSize:Int, success: (PaginationBean<LogTable>) -> Unit) {
        model.loadData(page, pageSize, success)
    }

}