package com.lwh.debugtools.ui.fragment.log.model

import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.base.thread.ThreadUtil
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.LogTable

/**
 * @author lwh
 * @Date 2019/10/25 11:06
 * @description LogModel
 */
class LogModel : BaseModelImpl() {

    fun loadData(page: Int, pageSize: Int, success: (PaginationBean<LogTable>) -> Unit) {
        ThreadUtil.queueWork(Runnable {
            val paginationBean: PaginationBean<LogTable> = DatabaseUtils.getLogs(page, pageSize)
            ThreadUtil.runMain {
                success(paginationBean)
            }
        })
    }
}