package com.lwh.debugtools.item.error

import android.view.View
import androidx.core.content.ContextCompat
import com.lwh.debugtools.R
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.time.TimeUtils
import com.lwh.debugtools.db.table.ErrorTable
import com.lwh.debugtools.ui.activity.error.DTErrorDetailsActivity
import kotlinx.android.synthetic.main.l_item_error_list.view.*

/**
 * @author lwh
 * @Date 2019/11/22 10:18
 * @description
 */
class ItemErrorList(private val model: ErrorTable) : BaseItem() {

    override fun layoutId(): Int = R.layout.l_item_error_list

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView;
        view.tv_throwable.text = model.throwable
        view.tv_crash_date.text = TimeUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS", model.crashDate)
        view.tv_crash_state.text = if (model.state == null || model.state == 0L) "未捕获" else "已捕获"
        view.tv_crash_state.setTextColor(
            ContextCompat.getColor(
                view.context,
                if (model.state == null || model.state == 0L) R.color.red else R.color.black
            )
        )
        view.tag = model
        view.setOnClickListener(onClick)
    }

    override fun onClick(v: View) {
        mContextWrap?.let {
            it.getFragment()?.let { fragment ->
                DTErrorDetailsActivity.startActivity(fragment, model._id)
            }
        }
    }

    override fun uniqueItem(): String? {
        return model._id.toString()
    }

    override fun uniqueContent(): String? {
        return model.toString()
    }

}