package com.lwh.debugtools.ui.activity.request.model

import android.text.TextUtils
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.base.thread.ThreadUtil
import com.lwh.debugtools.base.utils.json.JsonUtils
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.RequestTable
import com.lwh.debugtools.item.request.ItemCommonDetailsBody
import com.lwh.debugtools.item.request.ItemRequestDetailsHeader
import com.lwh.debugtools.item.request.PROMPT

/**
 * @author lwh
 * @Date 2019/10/21 9:44
 * @description RequestDetailsModel
 */
class RequestDetailsModel : BaseModelImpl() {

    private val data = ArrayList<BaseGroupItem>()


    fun loadData(success: (ArrayList<BaseGroupItem>) -> Unit, id: Int) {
        ThreadUtil.queueWork(Runnable {
            val table: RequestTable? = DatabaseUtils.getRequestTable(id)
            table?.let {
                val requestBodyJson =
                    when {
                        it.requestBody == null -> ""
                        it.requestBody!!.toByteArray().size < 100 * 1024 -> JsonUtils.stringToJSON(
                            it.requestBody
                        )
                        else -> PROMPT
                    }
                val responseBodyJson =
                    when {
                        it.responseBody == null -> ""
                        it.responseBody!!.toByteArray().size < 100 * 1024 -> JsonUtils.stringToJSON(
                            it.responseBody
                        )
                        else -> PROMPT
                    }
                data.clear()
                val groupItem = BaseGroupItem()
                data.add(groupItem)
                val headerModel = ItemRequestDetailsHeaderModel(it)
                groupItem.headerItem = ItemRequestDetailsHeader(headerModel)
                if (!TextUtils.isEmpty(it.requestHeader)) {
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Request Header",
                                it.requestHeader
                            )
                        )
                    )
                }
                if (!TextUtils.isEmpty(requestBodyJson)) {
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Request Body",
                                requestBodyJson
                            )
                        )
                    )
                }
                if (!TextUtils.isEmpty(it.responseHeader)) {
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Response Header",
                                it.responseHeader
                            )
                        )
                    )
                }
                if (!TextUtils.isEmpty(responseBodyJson)) {
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Response Body",
                                responseBodyJson
                            )
                        )
                    )
                }
                if (!TextUtils.isEmpty(it.errorMessage)) {
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Error Message",
                                it.errorMessage
                            )
                        )
                    )
                }
                ThreadUtil.runMain {
                    success(data)
                }
            }
        })
    }
}