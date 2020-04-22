package com.lwh.debugtools.item.request

import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.lwh.debugtools.base.holder.BaseViewHolder
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.text.StringUtils
import com.lwh.debugtools.R
import com.lwh.debugtools.ui.activity.request.model.ItemRequestDetailsHeaderModel
import kotlinx.android.synthetic.main.l_item_request_details_header.view.*

const val PROMPT: String = "内容大于100kb"
/**
 * @author lwh
 * @Date 2019/10/21 11:52
 * @description ItemRequestDetailsHeader
 */
class ItemRequestDetailsHeader(private val model: ItemRequestDetailsHeaderModel) : BaseItem(){

    override fun layoutId(): Int = R.layout.l_item_request_details_header

    override fun updateView(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        view.tv_url.text = model.url
        view.tv_method.text = "Method:${model.method}"
        view.tv_time_consuming.text = "Time:${model.getTimeConsumingStr()}"
        view.tv_content_length.text = "ContentLength:${model.getContentLengthStr()}"
        view.tv_decrypt_content_length.text = if (model.decryptContentLength == null || model.decryptContentLength == 0L) "" else "DecryptContentLength:${model.getDecryptContentLengthStr()}"
        view.tv_code.text = "Code:${model.getCodeStr()}"
        view.tv_media_type.text = "MediaType:${model.mediaType}"
        view.tv_url.setOnClickListener(onClick)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_url -> {
                copy(model.url)
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