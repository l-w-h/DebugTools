package com.lwh.debugtools.base.structure

/**
 * @author lwh
 * @Date 2019/11/5 10:21
 * @description 这个类是用来记录分组列表中组的结构的。
 * 通过GroupStructure记录每个组是否有头部，是否有尾部和子项的数量。从而能方便的计算
 * 列表的长度和每个组的组头、组尾和子项在列表中的位置。
 */
data class GroupStructure(var hasHeader:Boolean = false,var hasFooter:Boolean = false,var childrenCount:Int = 0)