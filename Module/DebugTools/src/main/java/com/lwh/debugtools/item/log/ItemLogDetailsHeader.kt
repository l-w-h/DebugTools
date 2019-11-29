package com.lwh.debugtools.item.log

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.time.TimeUtils
import com.lwh.debugtools.R
import com.lwh.debugtools.ui.activity.log.model.ItemLogDetailsHeaderModel
import kotlinx.android.synthetic.main.l_item_log_details_header.view.*
import com.lwh.debugtools.base.utils.text.StringUtils.autoSplitText

/**
 * @author lwh
 * @Date 2019/10/25 11:42
 * @description ItemLogDetailsHeader
 */
class ItemLogDetailsHeader (private val model: ItemLogDetailsHeaderModel): BaseItem() {

    override fun layoutId(): Int = R.layout.l_item_log_details_header

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view:View = holder.itemView
        view.tv_location.text = model.location
        view.tv_tag.text = model.tag
        view.tv_type.text = model.type
        view.tv_type.setTextColor(
            ContextCompat.getColor(
                view.context,
                if (TextUtils.equals(model.type, "Error")) R.color.red else R.color.black
            )
        )
        view.tv_date.text = TimeUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS", model.time)
        view.tv_location.autoSplitText()
    }

    override fun uniqueItem(): String? {
        return model.toString()
    }

    override fun uniqueContent(): String? {
        return model.toString()
    }
}