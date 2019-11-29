package com.lwh.debugtools.base.diff

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.lwh.debugtools.base.item.BaseItem
import com.lwh.debugtools.base.utils.md5.Md5Utils

/**
 * @author lwh
 * @Date 2019/10/19 15:30
 * @description 通用diffCallBack
 */


open class BaseDiffCallBack : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is BaseItem && newItem is BaseItem) {
            if (oldItem.uniqueItem() == null || newItem.uniqueItem() == null) return false
            return TextUtils.equals(Md5Utils.encrypt(oldItem.uniqueItem()), Md5Utils.encrypt(newItem.uniqueItem()))
        }
        return TextUtils.equals(Md5Utils.encrypt(oldItem.toString()), Md5Utils.encrypt(newItem.toString()))
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem is BaseItem && newItem is BaseItem) {
            if (oldItem.uniqueContent() == null || newItem.uniqueContent() == null) return false
            return TextUtils.equals(Md5Utils.encrypt(oldItem.uniqueContent()), Md5Utils.encrypt(newItem.uniqueContent()))
        }
        return TextUtils.equals(Md5Utils.encrypt(oldItem.toString()), Md5Utils.encrypt(newItem.toString()))
    }

}
