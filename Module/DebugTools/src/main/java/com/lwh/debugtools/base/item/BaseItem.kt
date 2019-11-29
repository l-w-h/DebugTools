package com.lwh.debugtools.base.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lwh.debugtools.base.context.ContextWrap
import com.lwh.debugtools.base.holder.BaseViewHolder

/**
 * @author lwh
 * @Date 2019/10/19 15:07
 * @description BaseItem
 */
abstract class BaseItem {

    /**
     * 上下文对象
     */
    var mContextWrap: ContextWrap? = null

    /**
     * 点击方法
     */
    var onClick: View.OnClickListener? = null

    /**
     * 布局id
     */
    abstract fun layoutId(): Int

    /**
     * item类型 默认布局Id
     */
    fun getViewType(): Int = layoutId()

    /**
     * 获取View
     */
    fun getView(parent: ViewGroup, viewType: Int): View =
        LayoutInflater.from(parent.context).inflate(layoutId(), parent, false)

    /**
     * 局部更新View
     */
    open fun updateView(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        updateView(holder, position)
    }

    /**
     * 更新View
     */
    abstract fun updateView(holder: BaseViewHolder, position: Int)

    /**
     * 点击方法回调
     */
    open fun onClick(v: View) {

    }

    /**
     * 差异对比中 areItemsTheSame，获取item中的唯一标识对比
     */
    open fun uniqueItem(): String? {
        return null
    }

    /**
     * 差异对比中 areContentsTheSame，item中的唯一标识一致时，判断内容是否一致
     */
    open fun uniqueContent(): String? {
        return null
    }

}