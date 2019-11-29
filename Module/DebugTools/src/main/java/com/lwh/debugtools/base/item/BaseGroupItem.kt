package com.lwh.debugtools.base.item

/**
 * @author lwh
 * @Date 2019/11/5 11:12
 * @description 组数据的实体类
 */
data class BaseGroupItem(
    var headerItem: BaseItem? = null,
    var footerItem: BaseItem? = null,
    var children: ArrayList<BaseItem> = ArrayList()
)