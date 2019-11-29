package com.lwh.debugtools.item.request

import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.lwh.debugtools.base.constant.Payloads
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.text.StringUtils
import com.lwh.debugtools.base.utils.text.StringUtils.setKeyWordColor
import com.lwh.debugtools.R
import com.lwh.debugtools.ui.activity.request.model.ItemCommonDetailsBodyModel
import kotlinx.android.synthetic.main.l_item_details.view.*

/**
 * @author lwh
 * @Date 2019/10/21 12:11
 * @description ItemCommonDetailsBody
 */
class ItemCommonDetailsBody(private val model: ItemCommonDetailsBodyModel) : BaseItem() {

    /**
     * 设置搜索关键词
     */
    fun setSearchContent(searchContent:String?){
        model.searchContent = searchContent
    }

    override fun layoutId(): Int = R.layout.l_item_details

    /**
     * 局部更新View
     */
    override fun updateView(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        val view: View = holder.itemView
        if (payloads.isNullOrEmpty()) {
            updateView(holder, position)
        } else if (TextUtils.equals(Payloads.SEARCH_CONTENT,payloads[0].toString())){
            //搜索
            view.tv_details.setKeyWordColor(
                model.content, model.searchContent,
                ContextCompat.getColor(view.context, R.color.red),
                view.tv_details_count
            )
        }
    }

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view: View = holder.itemView
        view.tv_details_title.text = model.title
        view.tv_details.setKeyWordColor(
            model.content, model.searchContent,
            ContextCompat.getColor(view.context, R.color.red),
            view.tv_details_count
        )
        view.tv_details.setOnClickListener(onClick)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_details -> {
                if (model.content != null) copy(model.content)
            }
        }
    }


    /**
     * 复制内容
     */
    fun copy(content: String) {
        mContextWrap?.let {
            if (TextUtils.equals(
                    PROMPT,
                    content
                ) || content.toByteArray().size > 100 * 1024
            ) {
                Toast.makeText(it.getActivity(), "内容过大，禁止复制", Toast.LENGTH_SHORT).show()
                return;
            }
            StringUtils.copyToClipboard(it.getActivity(), content)
            Toast.makeText(it.getActivity(), "已复制到剪切板", Toast.LENGTH_SHORT).show()
        }
    }

    override fun uniqueItem(): String? {
        return model.toString()
    }

    override fun uniqueContent(): String? {
        return model.toString()
    }

}