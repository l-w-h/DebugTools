package com.lwh.debugtools.base.mvp

import com.lwh.debugtools.base.mvp.BaseModelImpl

/**
 * @author lwh
 * @Date 2019/10/19 11:59
 * @description 分页模型
 */
open class BasePageModel : BaseModelImpl() {

    //<editor-fold defaultstate="collapsed" desc="分页数据">
    var page: Int = 1
    var pageSize: Int = 10
    var totalSize: Long = 0
    var pageNext: Boolean = false
    //</editor-fold>

    /**
     * 判断是否是第一页
     */
    fun firstPage():Boolean = page == 1

    /**
     * 判断数据是否为空
     */
    fun isEmpty():Boolean = totalSize == 0L

    fun reset(){
        page = 1
        totalSize = 0
        pageNext = false
    }
}