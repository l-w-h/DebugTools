package com.lwh.debugtools.db

import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.*

/**
 * @author lwh
 * @Date 2019/8/27 21:39
 * @description 数据库工具
 */
object DatabaseUtils {
    //<editor-fold defaultstate="collapsed" desc="网络请求部分">
    /**
     * 添加一个请求数据
     */
    fun putRequest(requestTable: RequestTable): Long {
        return DatabaseOpenHelper.getInstance().use {
            insertRequestTable(requestTable)
        }
    }

    /**
     * 获取请求列表
     */
    fun getRequests(page: Int, pageSize: Int): PaginationBean<RequestTable> {
        return DatabaseOpenHelper.getInstance().use {
            selectTableByPage<RequestTable>(
                RequestTableField.TABLE,
                RequestTable::class.java.name,
                RequestTableField.SENT_REQUEST_AT_MILLIS,
                page,
                pageSize
            )
        }
    }

    /**
     * 根据id获取相应的请求
     */
    fun getRequestTable(id: Int): RequestTable? {
        return DatabaseOpenHelper.getInstance().use {
            selectTableById(RequestTableField.TABLE, id, RequestTable::class.java.name)
        }
    }

    /**
     * 根据id删除数据
     */
    fun deleteRequestTableById(id: Int): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableById(RequestTableField.TABLE, id)
        }
    }

    /**
     * 删除所有请求数据
     */
    fun deleteRequestTableData(): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableData(RequestTableField.TABLE)
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Log部分">
    /**
     * 添加一个Log
     */
    fun putLog(logTable: LogTable): Long {
        return DatabaseOpenHelper.getInstance().use {
            insertLogTable(logTable)
        }
    }

    /**
     * 获取Log列表
     */
    fun getLogs(page: Int, pageSize: Int): PaginationBean<LogTable> {
        return DatabaseOpenHelper.getInstance().use {
            selectTableByPage(LogTableField.TABLE, LogTable::class.java.name, LogTableField.TIME, page, pageSize)
        }
    }

    /**
     * 根据id获取相应的请求
     */
    fun getLogTable(id: Int): LogTable? {
        return DatabaseOpenHelper.getInstance().use {
            selectTableById<LogTable>(LogTableField.TABLE, id, LogTable::class.java.name)
        }
    }

    /**
     * 根据id删除数据
     */
    fun deleteLogTableById(id: Int): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableById(LogTableField.TABLE, id)
        }
    }

    /**
     * 删除所有请求数据
     */
    fun deleteLogTableData(): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableData(LogTableField.TABLE)
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="异常部分">
    /**
     * 添加一个Log
     */
    fun putError(errorTable: ErrorTable): Long {
        return DatabaseOpenHelper.getInstance().use {
            insertErrorTable(errorTable)
        }
    }

    /**
     * 获取Log列表
     */
    fun getErrors(page: Int, pageSize: Int): PaginationBean<ErrorTable> {
        return DatabaseOpenHelper.getInstance().use {
            selectTableByPage(
                ErrorTableField.TABLE,
                ErrorTable::class.java.name,
                ErrorTableField.CRASH_DATE,
                page,
                pageSize
            )
        }
    }

    /**
     * 根据id获取相应的请求
     */
    fun getErrorTable(id: Int): ErrorTable? {
        return DatabaseOpenHelper.getInstance().use {
            selectTableById(ErrorTableField.TABLE, id, ErrorTable::class.java.name)
        }
    }

    /**
     * 根据id删除数据
     */
    fun deleteErrorTableById(id: Int): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableById(ErrorTableField.TABLE, id)
        }
    }

    /**
     * 删除所有请求数据
     */
    fun deleteErrorTableData(): Int {
        return DatabaseOpenHelper.getInstance().use {
            deleteTableData(ErrorTableField.TABLE)
        }
    }

    //</editor-fold>
}