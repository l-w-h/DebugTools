package com.lwh.debugtools.base.listener

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

/**
 * @author lwh
 * @Date 2019/10/21 9:21
 * @description RefreshListener
 */
interface RefreshListener {

    /**
     * 加载数据
     */
//    fun loadData()

    /**
     * 加载下一页数据
     */
    fun onLoadMore()

    /**
     * 刷新数据
     */
    fun onRefresh()

    /**
     * 设置布局管理器
     */
    fun setLayoutManager(context: Context): RecyclerView.LayoutManager

    /**
     * 对比差异结束
     */
//    fun onDifferEnd(@BaseAdapter.OperatingStatus status: Int)

    /**
     * 加载结束，头部和尾部恢复初始样式
     */
    fun loadEnd()
}