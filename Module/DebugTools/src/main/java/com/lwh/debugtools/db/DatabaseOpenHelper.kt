package com.lwh.debugtools.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.fragment.app.Fragment
import com.lwh.debugtools.db.table.ErrorTableField
import com.lwh.debugtools.db.table.LogTableField
import com.lwh.debugtools.db.table.RequestTableField
import com.lwh.debugtools.db.table.base.BaseTableField
import org.jetbrains.anko.db.*

/**
 * @author lwh
 * @Date 2019/8/25 16:19
 * @description DatabaseOpenHelper
 */
class DatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        /** 数据库名称 */
        private const val DB_NAME = "DebugDatabase"
        /** 数据库版本 */
        private const val DB_VERSION = 13
        /** 单例 */
        private var INSTANCE: DatabaseOpenHelper? = null

        private var mContext: Context? = null

        @Synchronized
        fun getInstance(ctx: Context? = mContext): DatabaseOpenHelper {
            if (INSTANCE == null) {
                INSTANCE = DatabaseOpenHelper(ctx!!.applicationContext)
            }
            return INSTANCE!!
        }

        val Context.database: DatabaseOpenHelper
            get() = getInstance(this)

        val Fragment.database: DatabaseOpenHelper
            get() = getInstance(this.context!!)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            RequestTableField.TABLE, false,
            BaseTableField.ID to INTEGER + PRIMARY_KEY + UNIQUE,
            BaseTableField.EXPAND to TEXT,
            RequestTableField.URL to TEXT,
            RequestTableField.HOST to TEXT,
            RequestTableField.PATH to TEXT,
            RequestTableField.QUERY to TEXT,
            RequestTableField.METHOD to TEXT,
            RequestTableField.REQUEST_HEADER to TEXT,
            RequestTableField.RESPONSE_HEADER to TEXT,
            RequestTableField.REQUEST_BODY to TEXT,
            RequestTableField.RESPONSE_BODY to TEXT,
            RequestTableField.MEDIA_TYPE to TEXT,
            RequestTableField.CONTENT_LENGTH to INTEGER,
            RequestTableField.CODE to INTEGER,
            RequestTableField.SENT_REQUEST_AT_MILLIS to INTEGER,
            RequestTableField.RECEIVED_RESPONSE_AT_MILLIS to INTEGER,
            RequestTableField.ERROR_MESSAGE to TEXT,
            RequestTableField.DECRYPT_CONTENT_LENGTH to INTEGER,
            RequestTableField.DECRYPT_RESPONSE_BODY to TEXT,
            RequestTableField.DECRYPT_REQUEST_BODY to TEXT
        )
        databaseUpgradeTo2(db)
        databaseUpgradeTo7(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val clazz = javaClass
        for (i in oldVersion + 1..newVersion) {
            try {
                val method = clazz.getDeclaredMethod("databaseUpgradeTo$i", SQLiteDatabase::class.java)
                method.isAccessible = true
                method.invoke(this, db)
                Log.i("onUpgrade", "oldVersion:$oldVersion upgradeTo:$i newVersion:$newVersion")
            } catch (e: Exception) {

            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="数据库每个升级版本">
    /**
     * 新建Log表
     */
    private fun databaseUpgradeTo2(db: SQLiteDatabase?) {
        db?.createTable(
            LogTableField.TABLE, false,
            BaseTableField.ID to INTEGER + PRIMARY_KEY + UNIQUE,
            BaseTableField.EXPAND to TEXT,
            LogTableField.CONTENT to TEXT,
            LogTableField.LOCATION to TEXT,
            LogTableField.TIME to INTEGER,
            LogTableField.TYPE to TEXT,
            LogTableField.TAG to TEXT,
            LogTableField.LOG_STACK to TEXT
        )
    }

    /**
     * Log表新增字段 tag
     */
    private fun databaseUpgradeTo3(db: SQLiteDatabase?) {
        val sql = "ALTER TABLE ${LogTableField.TABLE} ADD COLUMN ${LogTableField.TAG} TEXT"
        db?.execSQL(sql)
    }

    /**
     * Log表新增字段 logStack
     */
    private fun databaseUpgradeTo4(db: SQLiteDatabase?) {
        val sql = "ALTER TABLE ${LogTableField.TABLE} ADD COLUMN ${LogTableField.LOG_STACK} TEXT"
        db?.execSQL(sql)
    }

    /**
     * Request表新增字段 errorMessage
     */
    private fun databaseUpgradeTo5(db: SQLiteDatabase?) {
        val sql = "ALTER TABLE ${RequestTableField.TABLE} ADD COLUMN ${RequestTableField.ERROR_MESSAGE} TEXT"
        db?.execSQL(sql)
    }

    /**
     * Request新增字段 expand
     * Log表新增字段 expand
     */
    private fun databaseUpgradeTo6(db: SQLiteDatabase?) {
        val sql1 = "ALTER TABLE ${RequestTableField.TABLE} ADD COLUMN ${BaseTableField.EXPAND} TEXT"
        val sql2 = "ALTER TABLE ${LogTableField.TABLE} ADD COLUMN ${BaseTableField.EXPAND} TEXT"
        db?.execSQL(sql1)
        db?.execSQL(sql2)
    }

    /**
     * 新增异常表
     */
    private fun databaseUpgradeTo7(db: SQLiteDatabase?) {
        db?.createTable(
            ErrorTableField.TABLE, false,
            BaseTableField.ID to INTEGER + PRIMARY_KEY + UNIQUE,
            BaseTableField.EXPAND to TEXT,
            ErrorTableField.BUILD_VERSION to TEXT,
            ErrorTableField.BUILD_DATE to TEXT,
            ErrorTableField.DEVICE to TEXT,
            ErrorTableField.THROWABLE to TEXT,
            ErrorTableField.STACK to TEXT,
            ErrorTableField.USER_ACTIONS to TEXT,
            ErrorTableField.CRASH_DATE to INTEGER,
            ErrorTableField.STATE to INTEGER
        )
    }

    /**
     * Error新增字段 state
     */
    private fun databaseUpgradeTo8(db: SQLiteDatabase?) {
        val sql = "ALTER TABLE ${ErrorTableField.TABLE} ADD COLUMN ${ErrorTableField.STATE} INTEGER"
        db?.execSQL(sql)
    }

    /**
     * Error字段 历史数据state默认值
     */
    private fun databaseUpgradeTo9(db: SQLiteDatabase?) {
//        val sql = "UPDATE ${ErrorTableField.TABLE} SET ${ErrorTableField.STATE}=0 WHERE ${ErrorTableField.STATE}!=1"
//        db?.execSQL(sql)
        val contentValues = ContentValues()
        contentValues.put("${ErrorTableField.STATE}", 0)
        db?.update(ErrorTableField.TABLE, contentValues, "${ErrorTableField.STATE}!=?", arrayOf(1.toString()))
    }

    /**
     * Error字段 历史数据state默认值
     */
    private fun databaseUpgradeTo10(db: SQLiteDatabase?) {
        databaseUpgradeTo9(db)
    }

    /**
     * Error字段 历史数据state默认值
     */
    private fun databaseUpgradeTo11(db: SQLiteDatabase?) {
        databaseUpgradeTo9(db)
    }

    /**
     * Error字段 历史数据state默认值
     */
    private fun databaseUpgradeTo12(db: SQLiteDatabase?) {
        databaseUpgradeTo9(db)
    }

    /**
     * RequestTable 添加解密前的字段
     * [decryptContentLength,decryptRequestBody,decryptResponseBody]
     */
    private fun databaseUpgradeTo13(db: SQLiteDatabase?){
        val addDecryptRequestBody = "ALTER TABLE ${RequestTableField.TABLE} ADD COLUMN ${RequestTableField.DECRYPT_REQUEST_BODY} TEXT"
        db?.execSQL(addDecryptRequestBody)
        val addDecryptResponseBody = "ALTER TABLE ${RequestTableField.TABLE} ADD COLUMN ${RequestTableField.DECRYPT_RESPONSE_BODY} TEXT"
        db?.execSQL(addDecryptResponseBody)
        val addDecryptContentLength = "ALTER TABLE ${RequestTableField.TABLE} ADD COLUMN ${RequestTableField.DECRYPT_CONTENT_LENGTH} INTEGER"
        db?.execSQL(addDecryptContentLength)
    }

    //</editor-fold>

}