package com.lwh.debugtools.ui.fragment.request.model

import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.base.thread.ThreadUtil
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.RequestTable

/**
 * @author lwh
 * @Date 2019/10/19 16:21
 * @description RequestModel
 */
class RequestModel : BaseModelImpl() {

    fun loadData(page: Int, pageSize: Int, success: (PaginationBean<RequestTable>) -> Unit) {
        ThreadUtil.queueWork(Runnable{
            val paginationBean: PaginationBean<RequestTable> = DatabaseUtils.getRequests(page, pageSize)
            ThreadUtil.runMain {
                success(paginationBean)
            }
        })
    }

}