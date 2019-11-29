package com.lwh.debugtools.ui.fragment.error

import android.content.Intent
import com.lwh.debugtools.base.constant.RequestCode
import com.lwh.debugtools.base.constant.ResultCode
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.ui.RefreshFragment
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.ErrorTable
import com.lwh.debugtools.item.error.ItemErrorList
import com.lwh.debugtools.ui.fragment.error.presenter.ErrorPresenter

/**
 * @author lwh
 * @Date 2019/11/22 10:13
 * @description error列表
 */
class ErrorFragment : RefreshFragment<BaseDiffCallBack>() {


    private val errorPresenter = ErrorPresenter()

    companion object {
        fun newInstance() = ErrorFragment()
    }

    override fun initBefore() {
        showHeaderView = false
    }

    override fun init() {

    }

    override fun loadData(page: Int, pageSize: Int) {
        errorPresenter.loadData(page, pageSize, ::success)
    }

    fun success(paginationBean: PaginationBean<ErrorTable>) {
        refreshManager.pageModel.totalSize = paginationBean.total
        refreshManager.pageModel.pageNext = paginationBean.next
        val firstPage = refreshManager.pageModel.firstPage()

        val items = ArrayList<BaseGroupItem>()
        val groupItem = BaseGroupItem()
        paginationBean.record.forEach {
            val itemErrorList = ItemErrorList(it)
            groupItem.children.add(itemErrorList)
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