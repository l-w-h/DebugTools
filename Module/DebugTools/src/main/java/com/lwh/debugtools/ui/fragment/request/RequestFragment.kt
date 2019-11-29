package com.lwh.debugtools.ui.fragment.request

import android.content.Intent
import com.lwh.debugtools.base.constant.RequestCode
import com.lwh.debugtools.base.constant.ResultCode
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.ui.RefreshFragment
import com.lwh.debugtools.bean.PaginationBean
import com.lwh.debugtools.db.table.RequestTable
import com.lwh.debugtools.diff.RequestDiffCallBack
import com.lwh.debugtools.item.request.ItemRequestList
import com.lwh.debugtools.ui.fragment.request.presenter.RequestPresenter

/**
 * @author lwh
 * @Date 2019/10/21 16:13
 * @description 请求列表
 */
class RequestFragment : RefreshFragment<RequestDiffCallBack>() {

    private val presenter: RequestPresenter = RequestPresenter()

    companion object {
        fun newInstance() =
            RequestFragment()
    }

    override fun loadData(page: Int, pageSize: Int) {
        presenter.loadData(page, pageSize, ::success)
    }

    override fun initBefore() {
        showHeaderView = false
    }

    override fun init() {
    }

    private fun success(paginationBean: PaginationBean<RequestTable>) {
        refreshManager.pageModel.totalSize = paginationBean.total
        refreshManager.pageModel.pageNext = paginationBean.next
        val firstPage = refreshManager.pageModel.firstPage()

        val items = ArrayList<BaseGroupItem>()
        val groupItem = BaseGroupItem()
        paginationBean.record.forEach {
            val itemRequestList = ItemRequestList(it)
            groupItem.children.add(itemRequestList)
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
