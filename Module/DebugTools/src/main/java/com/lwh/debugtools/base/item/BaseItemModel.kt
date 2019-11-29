package com.lwh.debugtools.base.item

import com.google.gson.Gson

/**
 * @author lwh
 * @Date 2019/10/21 11:55
 * @description BaseItemModel
 */
abstract class BaseItemModel{
    override fun toString(): String {
        return Gson().toJson(this)
    }
}