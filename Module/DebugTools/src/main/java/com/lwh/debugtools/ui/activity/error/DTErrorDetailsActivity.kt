package com.lwh.debugtools.ui.activity.error

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lwh.debugtools.base.constant.Payloads
import com.lwh.debugtools.base.constant.RequestCode
import com.lwh.debugtools.base.constant.ResultCode
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.item.BaseGroupItem
import com.lwh.debugtools.base.ui.RefreshActivity
import com.lwh.debugtools.R
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.item.request.ItemCommonDetailsBody
import com.lwh.debugtools.ui.activity.error.presenter.ErrorDetailsPresenter

/**
 * @author lwh
 * @Date 2019/10/25 11:33
 * @description 崩溃详情页面
 */
class DTErrorDetailsActivity : RefreshActivity<BaseDiffCallBack>() {

    private val errorDetailsPresenter = ErrorDetailsPresenter()

    /**
     * error id
     */
    var id: Int = 0

    /**
     * 搜索输入框
     */
    private var searchPopup: AlertDialog? = null

    /**
     * 搜索内容
     */
    private var searchContent: String? = null

    companion object {


        /**
         * 参数Key id
         */
        const val PARAM_ID: String = "id"

        fun startActivity(fragment: Fragment, id: Int) {
            val intent = Intent(fragment.context, DTErrorDetailsActivity::class.java)
            intent.putExtra(PARAM_ID, id)
            fragment.startActivityForResult(intent, RequestCode.CODE_CALL_BACK)
        }
    }

    override fun initParam() {
        id = intent.getIntExtra(PARAM_ID, 0)
    }

    override fun init() {
        headerManager?.apply {
            title = "Error Details"
            setFuncAText(R.string.icon_delete)
            setFuncBText(R.string.icon_search)
            setFuncAVisible(View.VISIBLE)
            setFuncBVisible(View.VISIBLE)
        }
    }

    override fun loadData(page: Int, pageSize: Int) {
        errorDetailsPresenter.loadData(id, ::success)
    }

    private fun success(data: ArrayList<BaseGroupItem>) {
        refreshManager.adapter.setList(data)
    }


    override fun funcBClick(v: View) {

        if (searchPopup == null) {
            val editText = EditText(this)
            editText.setSingleLine()
            editText.hint = "例：${getString(R.string.app_name)}"
            editText.post {
                val lp: ViewGroup.MarginLayoutParams = editText.layoutParams as ViewGroup.MarginLayoutParams
                lp.leftMargin = resources.getDimensionPixelSize(R.dimen.dp_16)
                lp.rightMargin = resources.getDimensionPixelSize(R.dimen.dp_16)
            }
            searchPopup = AlertDialog.Builder(this)
                .setTitle("搜索")
                .setMessage("请输入搜索内容")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
                    searchContent = editText.text.toString()
                    rootView.postDelayed(Runnable {
                        refreshManager.adapter.groupItems.forEachIndexed { index, baseItem ->
                            baseItem.children.forEachIndexed { childIndex, childItem ->
                                if (childItem is ItemCommonDetailsBody) {
                                    childItem.setSearchContent(searchContent)
                                    refreshManager.adapter.notifyChildChanged(
                                        index,
                                        childIndex,
                                        Payloads.SEARCH_CONTENT
                                    )
                                }
                            }
                        }
                    }, 200)
                }.setNegativeButton("取消", null)
                .create()
        }
        searchPopup?.show()
    }

    override fun funcAClick(v: View) {

        val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("删除数据")
            .setMessage("确定要永久删除此数据吗？")
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ ->
                DatabaseUtils.deleteErrorTableById(id)
                setResult(ResultCode.CODE_DELETE)
                finish()
            }.setNegativeButton("取消", null)
            .create()
        alertDialog.show()
    }

}