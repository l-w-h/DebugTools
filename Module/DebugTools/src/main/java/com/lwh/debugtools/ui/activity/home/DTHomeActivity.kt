package com.lwh.debugtools.ui.activity.home

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.lwh.debugtools.R
import com.lwh.debugtools.base.adapter.ViewPagerAdapter
import com.lwh.debugtools.base.listener.OnPageChangeListenerImpl
import com.lwh.debugtools.base.ui.HeaderFooterActivity
import com.lwh.debugtools.base.ui.RefreshFragment
import com.lwh.debugtools.db.DatabaseUtils
import com.lwh.debugtools.ui.fragment.error.ErrorFragment
import com.lwh.debugtools.ui.fragment.log.LogFragment
import com.lwh.debugtools.ui.fragment.request.RequestFragment
import kotlinx.android.synthetic.main.l_activity_home.*

/**
 * @author lwh
 * @Date 2019/10/21 9:41
 * @description Debug Tools 首页（请求，日志，错误）
 */
class DTHomeActivity : HeaderFooterActivity(), OnPageChangeListenerImpl, View.OnClickListener {

    private val fragments = ArrayList<RefreshFragment<*>>()

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, DTHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun initBefore() {
        bodyLayout = R.layout.l_activity_home
    }


    override fun init() {
        headerManager?.let {
            it.setFuncAText(R.string.icon_delete)
            it.setFuncAVisible(View.VISIBLE)
        }
        fragments.add(RequestFragment.newInstance())
        fragments.add(LogFragment.newInstance())
        fragments.add(ErrorFragment.newInstance())
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager, null, fragments)
        view_pager.addOnPageChangeListener(this)

        headerManager?.title = "Debug Tools"

        ll_request.setOnClickListener(this)
        ll_log.setOnClickListener(this)
        ll_error.setOnClickListener(this)
        selectorPage(0)
    }

    /**
     * 重置选中按钮
     */
    private fun resetNavView() {
        icon_request.isChecked = false
        tv_request.isChecked = false
        icon_log.isChecked = false
        tv_log.isChecked = false
        icon_error.isChecked = false
        tv_error.isChecked = false
    }

    /**
     * 根据下标选中按钮
     */
    private fun selectorPage(position: Int) {
        resetNavView()
        when (position) {
            0 -> {
                icon_request.isChecked = true
                tv_request.isChecked = true
            }
            1 -> {
                icon_log.isChecked = true
                tv_log.isChecked = true
            }
            2 -> {
                icon_error.isChecked = true
                tv_error.isChecked = true
            }
            else -> {
            }
        }
    }

    override fun onClick(view: View?) {
        resetNavView()
        val position: Int = when (view?.id) {
            R.id.ll_request -> {
                0
            }
            R.id.ll_log -> {
                1
            }
            R.id.ll_error -> {
                2
            }
            else -> {
                0
            }
        }
        selectorPage(position)
        view_pager.currentItem = position
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        selectorPage(position)
    }


    override fun funcAClick(v: View) {

        val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("删除数据")
            .setMessage("确定要永久删除全部数据吗？")
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ ->
                val currentItem = view_pager.currentItem
                when (currentItem) {
                    0 -> {
                        DatabaseUtils.deleteRequestTableData()
                    }
                    1 -> {
                        DatabaseUtils.deleteLogTableData()
                    }
                    2 -> {
                        DatabaseUtils.deleteErrorTableData()
                    }
                }
                fragments[currentItem].reLoadData()
            }.setNegativeButton("取消", null)
            .create()
        alertDialog.show()
    }
}
