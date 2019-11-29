package com.lwh.debugtools.item.log

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.time.TimeUtils
import com.lwh.debugtools.R
import com.lwh.debugtools.db.table.LogTable
import com.lwh.debugtools.ui.activity.log.DTLogDetailsActivity
import kotlinx.android.synthetic.main.l_item_log_list.view.*

/**
 * @author lwh
 * @Date 2019/10/19 16:44
 * @description ItemLogList
 */
class ItemLogList(private val model: LogTable) : BaseItem() {

    override fun layoutId(): Int = R.layout.l_item_log_list

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView;
        view.tv_type.text = model.type
        view.tv_type.setTextColor(
            ContextCompat.getColor(
                view.tv_type.context,
                if (TextUtils.equals(model.type, "Error")) R.color.red else R.color.black
            )
        )
        view.tv_tag.text = model.tag
        view.tv_content.text = model.content
        view.tv_time.text = TimeUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS", model.time)
        view.tag = model
        view.setOnClickListener(onClick)
    }

    override fun onClick(v: View) {
        mContextWrap?.let {
            it.getFragment()?.let { fragment ->
                DTLogDetailsActivity.startActivity(fragment,model._id)
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