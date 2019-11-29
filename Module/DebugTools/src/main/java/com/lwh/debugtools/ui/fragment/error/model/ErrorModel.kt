package com.lwh.debugtools.ui.fragment.error.model

import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.ErrorTable

/**
 * @author lwh
 * @Date 2019/11/22 10:15
 * @description
 */
class ErrorModel : BaseModelImpl() {

    fun loadData(page: Int, pageSize: Int, success: (PaginationBean<ErrorTable>) -> Unit) {
        val paginationBean: PaginationBean<ErrorTable> = DatabaseUtils.getErrors(page, pageSize)
        success(paginationBean)
    }
}