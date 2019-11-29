package com.lwh.debugtools.ui.activity.request.model

import com.lwh.debugtools.base.item.BaseItemModel

/**
 * @author lwh
 * @Date 2019/10/21 12:08
 * @description ItemCommonDetailsBodyModel
 */
data class ItemCommonDetailsBodyModel(val title:String, val content:String?, var searchContent:String? = null) :
    BaseItemModel()