package com.lwh.debugtools.item.request

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.time.TimeUtils
import com.lwh.debugtools.base.utils.unit.UnitUtils
import com.lwh.debugtools.R
import com.lwh.debugtools.db.table.RequestTable
import com.lwh.debugtools.ui.activity.request.DTRequestDetailsActivity
import kotlinx.android.synthetic.main.l_item_request_list.view.*

/**
 * @author lwh
 * @Date 2019/10/19 16:44
 * @description ItemRequestList
 */
class ItemRequestList(private val requestTable: RequestTable) : BaseItem() {

    override fun layoutId(): Int = R.layout.l_item_request_list

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView;
        view.tv_code.text = requestTable.code.toString()
        view.tv_code.setTextColor(
            ContextCompat.getColor(
                view.context,
                if (requestTable.code == 200L) R.color.green else R.color.red
            )
        )
        view.tv_content_length.text =
            UnitUtils.fileSize(if (requestTable.contentLength != null) requestTable.contentLength!!.toDouble() else 0.toDouble())
        view.tv_method.text = requestTable.method
        view.tv_start_time.text = TimeUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS", requestTable.sentRequestAtMillis!!)
        view.tv_time_consuming.text =
            UnitUtils.timeUnit(requestTable.receivedResponseAtMillis!! - requestTable.sentRequestAtMillis!!)
        view.tv_host.text = requestTable.host
        view.tv_path.text = requestTable.path
        view.tv_error.visibility = if (TextUtils.isEmpty(requestTable.errorMessage)) View.GONE else View.VISIBLE
        view.tag = requestTable
        view.setOnClickListener(onClick)

    }

    override fun onClick(v: View) {
        mContextWrap?.let {
            it.getFragment()?.let {fragment->
                DTRequestDetailsActivity.startActivity(fragment, requestTable._id)
            }
        }
    }

    override fun uniqueItem(): String? {
        return requestTable._id.toString()
    }

    override fun uniqueContent(): String? {
        return requestTable.toString()
    }

}