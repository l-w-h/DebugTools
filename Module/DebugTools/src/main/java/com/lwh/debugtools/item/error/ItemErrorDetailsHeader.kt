package com.lwh.debugtools.item.error

import android.view.View
import androidx.core.content.ContextCompat
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.text.StringUtils.autoSplitText
import com.lwh.debugtools.base.utils.time.TimeUtils
import com.lwh.debugtools.R
import com.lwh.debugtools.ui.activity.error.model.ItemErrorDetailsHeaderModel
import kotlinx.android.synthetic.main.l_item_error_header.view.*

/**
 * @author lwh
 * @Date 2019/10/25 11:42
 * @description ItemLogDetailsHeader
 */
class ItemErrorDetailsHeader(private val model: ItemErrorDetailsHeaderModel) : BaseItem() {

    override fun layoutId(): Int = R.layout.l_item_error_header

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view: View = holder.itemView
        view.tv_throwable.text = model.throwable
        view.tv_version.text = "version:${model.buildVersion}"
        view.tv_build_date.text = "updateDate:${model.buildDate}"
        view.tv_device.text = model.device
        view.tv_crash_date.text = "crashDate:${TimeUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS", model.crashDate)}"
        view.tv_crash_state.text = if (model.state == null || model.state == 0L) "未捕获" else "已捕获"
        view.tv_crash_state.setTextColor(
            ContextCompat.getColor(
                view.context,
                if (model.state == null || model.state == 0L) R.color.red else R.color.black
            )
        )
        view.tv_throwable.autoSplitText()
    }

    override fun uniqueItem(): String? {
        return model.toString()
    }

    override fun uniqueContent(): String? {
        return model.toString()
    }
}