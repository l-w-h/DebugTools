package com.lwh.debugtools.ui.activity.log.model

import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.mvp.BaseModelImpl
import com.lwh.debugtools.base.thread.ThreadUtil
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.db.table.LogTable
import com.lwh.debugtools.item.log.ItemLogDetailsHeader
import com.lwh.debugtools.item.request.ItemCommonDetailsBody
import com.lwh.debugtools.ui.activity.request.model.ItemCommonDetailsBodyModel

/**
 * @author lwh
 * @Date 2019/10/25 11:38
 * @description LogDetailsModel
 */
class LogDetailsModel : BaseModelImpl() {

    private val data = ArrayList<BaseGroupItem>()

    fun loadData(id: Int, success: (ArrayList<BaseGroupItem>) -> Unit) {
        ThreadUtil.queueWork(Runnable {
            val model: LogTable? = DatabaseUtils.getLogTable(id)
            model?.let {
                data.clear()
                val groupItem = BaseGroupItem()
                data.add(groupItem)
                groupItem.headerItem = ItemLogDetailsHeader(ItemLogDetailsHeaderModel(it))
                it.content?.let { content ->
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Content",
                                content
                            )
                        )
                    )
                }
                it.logStack?.let { logStack ->
                    groupItem.children.add(
                        ItemCommonDetailsBody(
                            ItemCommonDetailsBodyModel(
                                "Stack",
                                logStack
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