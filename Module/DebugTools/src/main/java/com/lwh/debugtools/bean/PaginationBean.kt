package com.lwh.debugtools.bean

/**
 * @author lwh
 * @Date 2019/9/2 9:35
 * @description 分页bean
 */
data class PaginationBean<Mode>(
    val record: List<Mode>,
    val pages: Int,
    val total: Long,
    val currentPage: Int,
    val pageSize: Int,
    val next: Boolean,
    val prev: Boolean
)