package com.lwh.debugtools.ui.fragment.log

import android.content.Intent
import com.lwh.debugtools.base.constant.RequestCode
import com.lwh.debugtools.base.constant.ResultCode
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.ui.RefreshFragment
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.LogTable
import com.lwh.debugtools.item.log.ItemLogList
import com.lwh.debugtools.ui.fragment.log.presenter.LogPresenter

/**
 * @author lwh
 * @Date 2019/10/25 11:04
 * @description Log列表
 */
class LogFragment : RefreshFragment<BaseDiffCallBack>() {

    private val logPresenter = LogPresenter()

    companion object {
        fun newInstance() = LogFragment()
    }

    override fun initBefore() {
        showHeaderView = false
    }

    override fun init() {

    }

    override fun loadData(page: Int, pageSize: Int) {
        logPresenter.loadData(page, pageSize, ::success)
    }

    fun success(paginationBean: PaginationBean<LogTable>) {
        refreshManager.pageModel.totalSize = paginationBean.total
        refreshManager.pageModel.pageNext = paginationBean.next
        val firstPage = refreshManager.pageModel.firstPage()

        val items = ArrayList<BaseGroupItem>()
        val groupItem = BaseGroupItem()
        paginationBean.record.forEach {
            val itemLogList = ItemLogList(it)
            groupItem.children.add(itemLogList)
        }
        items.add(groupItem)
        if (firstPage || refreshManager.pageModel.isEmpty()) {
            refreshManager.adapter.setList(items)
        } else {
            refreshManager.adapter.addList(items)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.CODE_CALL_BACK && resultCode == ResultCode.CODE_DELETE) {
            refreshManager.pageModel.page = 1
            reLoadData()
        }
    }


}