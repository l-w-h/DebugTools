package com.lwh.debugtools.base.mvp

import com.google.gson.Gson
import com.lwh.debugtools.base.mvp.BaseModel

/**
 * @author lwh
 * @Date 2019/10/21 9:27
 * @description BaseModelImpl
 */
open class BaseModelImpl : BaseModel {

    override fun toString(): String {
        return Gson().toJson(this)
    }
}