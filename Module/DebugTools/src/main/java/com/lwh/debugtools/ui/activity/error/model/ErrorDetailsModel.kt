package com.lwh.debugtools.ui.activity.error.model

import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.base.thread.ThreadUtil
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.ErrorTable
import com.lwh.debugtools.item.error.ItemErrorDetailsHeader
import com.lwh.debugtools.item.request.ItemCommonDetailsBody
import com.lwh.debugtools.ui.activity.request.model.ItemCommonDetailsBodyModel

/**
 * @author lwh
 * @Date 2019/11/22 10:21
 * @description
 */
class ErrorDetailsModel : BaseModelImpl(){

    private val data = ArrayList<BaseGroupItem>()

    fun loadData(id: Int, success: (ArrayList<BaseGroupItem>) -> Unit) {
        ThreadUtil.queueWork(Runnable {
            val model: ErrorTable? = DatabaseUtils.getErrorTable(id)
            model?.let {
                data.clear()
                val groupItem = BaseGroupItem()
                data.add(groupItem)
                groupItem.headerItem = ItemErrorDetailsHeader(ItemErrorDetailsHeaderModel(it))
                it.stack?.let { stack ->
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Stack",
                                stack
                            )
                        )
                    )
                }
                it.userActions?.let { userActions ->
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "UserActions",
                                userActions
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