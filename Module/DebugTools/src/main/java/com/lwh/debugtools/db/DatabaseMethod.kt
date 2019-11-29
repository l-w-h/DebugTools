package com.lwh.debugtools.db

import android.database.sqlite.SQLiteDatabase
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.*
import com.lwh.debugtools.db.table.base.BaseTableField
import org.jetbrains.anko.db.*
import java.io.Serializable
import java.math.BigDecimal

/**
 * @author lwh
 * @Date 2019/8/25 16:12
 * @description 数据库增删改查方法
 */

//<editor-fold defaultstate="collapsed" desc="通用查询">
/**
 * 查询总条数
 */
fun SQLiteDatabase.selectTableCount(table: String): Long {
    val column = "count()"
    return select(table, column)
        .parseSingle(object : MapRowParser<Long> {
            override fun parseRow(columns: Map<String, Any?>): Long {
                var countAny = columns.get(column)
                if (countAny is Long) {
                    return countAny
                }
                return 0L
            }
        })
}

/**
 * 分页查询网络请求
 */
fun <Mode : Serializable> SQLiteDatabase.selectTableByPage(
    table: String,
    tableClass: String,
    orderByColumn: String,
    page: Int,
    pageSize: Int
): PaginationBean<Mode> {
    val count = selectTableCount(table)
    val tables: List<Mode> = select(table)
        .orderBy(orderByColumn, SqlOrderDirection.DESC)
        .limit((page - 1) * pageSize, pageSize)
        .parseList(object : MapRowParser<Mode> {
            override fun parseRow(columns: Map<String, Any?>): Mode {
                return initTable(tableClass, columns)
            }

        })
    val pages: Int = (count / pageSize.toDouble()).toBigDecimal().setScale(0, BigDecimal.ROUND_UP).intValueExact()
    return PaginationBean<Mode>(tables, pages, count, page, pageSize, (page < pages), (page > 1 && pages > 1))
}

/** 根据ID查数据 */
fun <Table : Serializable> SQLiteDatabase.selectTableById(table: String, id: Int, tableClass: String): Table? {
    return select(table)
        .whereSimple("${BaseTableField.ID} = ?", "$id").parseOpt(object : MapRowParser<Table> {
            override fun parseRow(columns: Map<String, Any?>): Table {
                return initTable(tableClass, columns)
            }
        })
}

private fun <Table : Serializable> initTable(tableClass: String, columns: Map<String, Any?>): Table {
    val clazz: Class<Table> = Class.forName(tableClass) as Class<Table>
    val method = clazz.getDeclaredConstructor(MutableMap::class.java)
    method.isAccessible = true
    return method.newInstance(columns.toMutableMap())
}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="通用删除">

/**
 * 根据id删除数据
 */
fun SQLiteDatabase.deleteTableById(
    table: String,
    id: Int
): Int {
    return delete(table, "${BaseTableField.ID}=?", arrayOf(id.toString()))
}

/**
 * 删除某个表的所有数据
 */
fun SQLiteDatabase.deleteTableData(table: String): Int {
    return delete(table, "")
}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="insert">

/**
 * 插入一条新的请求数据
 */
fun SQLiteDatabase.insertRequestTable(requestTable: RequestTable): Long {
    return DatabaseOpenHelper.getInstance().use {
        insert(
            RequestTableField.TABLE,
            Pair(RequestTableField.URL, requestTable.url),
            Pair(RequestTableField.HOST, requestTable.host),
            Pair(RequestTableField.PATH, requestTable.path),
            Pair(RequestTableField.QUERY, requestTable.query),
            Pair(RequestTableField.METHOD, requestTable.method),
            Pair(RequestTableField.REQUEST_HEADER, requestTable.requestHeader),
            Pair(RequestTableField.RESPONSE_HEADER, requestTable.responseHeader),
            Pair(RequestTableField.REQUEST_BODY, requestTable.requestBody),
            Pair(RequestTableField.RESPONSE_BODY, requestTable.responseBody),
            Pair(RequestTableField.MEDIA_TYPE, requestTable.mediaType),
            Pair(RequestTableField.CONTENT_LENGTH, requestTable.contentLength),
            Pair(RequestTableField.CODE, requestTable.code),
            Pair(RequestTableField.SENT_REQUEST_AT_MILLIS, requestTable.sentRequestAtMillis),
            Pair(RequestTableField.RECEIVED_RESPONSE_AT_MILLIS, requestTable.receivedResponseAtMillis),
            Pair(RequestTableField.ERROR_MESSAGE, requestTable.errorMessage)
        )
    }
}

/**
 * 插入一条新的Log
 */
fun SQLiteDatabase.insertLogTable(logTable: LogTable): Long {
    return DatabaseOpenHelper.getInstance().use {
        insert(
            LogTableField.TABLE,
            Pair(LogTableField.TIME, logTable.time),
            Pair(LogTableField.CONTENT, logTable.content),
            Pair(LogTableField.LOCATION, logTable.location),
            Pair(LogTableField.TYPE, logTable.type),
            Pair(LogTableField.TAG, logTable.tag),
            Pair(LogTableField.LOG_STACK, logTable.logStack),
            Pair(BaseTableField.EXPAND, logTable.expand)
        )
    }
}

/**
 * 插入一条新的Error
 */
fun SQLiteDatabase.insertErrorTable(errorTable: ErrorTable): Long {
    return DatabaseOpenHelper.getInstance().use {
        insert(
            ErrorTableField.TABLE,
            Pair(BaseTableField.EXPAND, errorTable.expand),
            Pair(ErrorTableField.BUILD_VERSION, errorTable.buildVersion),
            Pair(ErrorTableField.BUILD_DATE, errorTable.buildDate),
            Pair(ErrorTableField.DEVICE, errorTable.device),
            Pair(ErrorTableField.THROWABLE, errorTable.throwable),
            Pair(ErrorTableField.STACK, errorTable.stack),
            Pair(ErrorTableField.USER_ACTIONS, errorTable.userActions),
            Pair(ErrorTableField.CRASH_DATE, errorTable.crashDate),
            Pair(ErrorTableField.STATE, errorTable.state)
        )
    }
}
//</editor-fold>
