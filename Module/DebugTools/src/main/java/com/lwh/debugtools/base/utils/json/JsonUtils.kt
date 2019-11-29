package com.lwh.debugtools.base.utils.json

/**
 * @author lwh
 * @Date 2019/8/25 14:54
 * @description json格式化
 */

object JsonUtils {

    /**
     * @date  2017/8/24
     * @description 将字符串格式化成JSON的格式
     */
    fun stringToJSON(strJson: String?): String {
        if(strJson == null){
            return "";
        }
        // 计数tab的个数
        var tabNum = 0
        val jsonFormat = StringBuffer()
        val length = strJson.length

        var last: Char = 0.toChar()
        for (i in 0 until length) {
            val c = strJson[i]
            if (c == '{') {
                tabNum++
                jsonFormat.append(c + "\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == '}') {
                tabNum--
                jsonFormat.append("\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
                jsonFormat.append(c)
            } else if (c == ',') {
                jsonFormat.append(c + "\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == ':') {
                jsonFormat.append("$c ")
            } else if (c == '[') {
                tabNum++
                val next = strJson[i + 1]
                if (next == ']') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append(c + "\n")
                    jsonFormat.append(getSpaceOrTab(tabNum))
                }
            } else if (c == ']') {
                tabNum--
                if (last == '[') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c)
                }
            } else {
                jsonFormat.append(c)
            }
            last = c
        }
        return jsonFormat.toString()
    }

    private fun getSpaceOrTab(tabNum: Int): String {
        val sbTab = StringBuffer()
        for (i in 0 until tabNum) {
            sbTab.append('\t')
        }
        return sbTab.toString()
    }

}
