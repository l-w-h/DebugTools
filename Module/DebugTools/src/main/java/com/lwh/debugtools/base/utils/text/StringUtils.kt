package com.lwh.debugtools.base.utils.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorInt
import com.lwh.debugtools.base.thread.ThreadUtil
import java.util.regex.Pattern


/**
 * @author lwh
 * @Date 2019/8/29 20:58
 * @description StringUtils
 */
object StringUtils {


    /**
     * 设置搜索关键字高亮
     *
     * @param content  原文本内容
     * @param keyword  关键字
     * @param keyColor 高亮颜色
     */
    fun TextView.setKeyWordColor(
        content: String?,
        keyword: String?, @ColorInt keyColor: Int,
        countView: TextView? = null
    ) {

        if (content.isNullOrEmpty() || keyColor == 0) {
            text = ""
            countView?.apply {
                text = ""
            }
            return
        }
        var key = ""
        if (!keyword.isNullOrEmpty()) {
            key = keyword
        } else {
            text = content
            countView?.apply {
                text = ""
            }
            return
        }

        ThreadUtil.queueWork(Runnable {
            val startMillis = System.currentTimeMillis()

            val s = SpannableString(content)
            //Pattern.CASE_INSENSITIVE 不区分大小写
            val contents = content.split(key)
            var index = 0
            if (contents.count() == 1) {
                if (TextUtils.equals(key, content)) {
                    s.setSpan(
                        ForegroundColorSpan(keyColor),
                        0,
                        content.count(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else {
                for (str in contents) {
                    var start = index + str.length
                    val end = start + key.length
                    if (end > content.length) {
                        continue
                    }
                    s.setSpan(
                        ForegroundColorSpan(keyColor),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    index = end
                }
            }

//            val p = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)
//            val m = p.matcher(s)
//            while (m.find()) {
//                val start = m.start()
//                val end = m.end()
//                s.setSpan(
//                    ForegroundColorSpan(keyColor),
//                    start,
//                    end,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//            }
            Log.i("DebugTag", "find 耗时：${System.currentTimeMillis() - startMillis}ms")

            post {
                text = s
                Log.i("DebugTag", "text 耗时：${System.currentTimeMillis() - startMillis}ms")
                countView?.apply {
                    if (contents.isNotEmpty()) {
                        text = "搜索结果${contents.size - 1}个"
                    } else {
                        text = ""
                    }
                }
            }
        })

    }

    /**
     * 复制到剪贴板
     */
    fun copyToClipboard(context: Context, content: String) {
        //获取剪贴板管理器：
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
    }


    /**
     * 半角转全角
     */
    fun ToDBC(input: String?): String? {
        input?.let {
            val c = input.toCharArray()
            for (i in c.indices) {
                if (c[i].toInt() == 12288) {
                    c[i] = 32.toChar()
                    continue
                }
                if (c[i].toInt() in 65281..65374)
                    c[i] = (c[i].toInt() - 65248).toChar()
            }
            return String(c)
        }
        return input
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     */
    fun stringFilter(str: String?): String? {
        str?.let {
            var s = it.replace("【".toRegex(), "[").replace("】".toRegex(), "]")
                .replace("！".toRegex(), "!").replace("：".toRegex(), ":")// 替换中文标号
            val regEx = "[『』]" // 清除掉特殊字符
            val p = Pattern.compile(regEx)
            val m = p.matcher(s)
            return m.replaceAll("").trim()
        }
        return str
    }

    /**
     * TextView中英文混合换行问题
     */
    fun TextView.autoSplitText() {
        post {
            val rawText = text.toString() //原始文本
            val tvPaint = paint //paint，包含字体等信息
            val tvWidth = width - paddingLeft - paddingRight //控件可用宽度
            //将原始文本按行拆分
            val rawTextLines =
                rawText.replace("\r".toRegex(), "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sbNewText = StringBuilder()
            for (rawTextLine in rawTextLines) {
                if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                    //如果整行宽度在控件可用宽度之内，就不处理了
                    sbNewText.append(rawTextLine)
                } else {
                    //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                    var lineWidth = 0f
                    var cnt = 0
                    while (cnt != rawTextLine.length) {
                        val ch = rawTextLine.get(cnt)
                        lineWidth += tvPaint.measureText(ch.toString())
                        if (lineWidth <= tvWidth) {
                            sbNewText.append(ch)
                        } else {
                            sbNewText.append("\n")
                            lineWidth = 0f
                            --cnt
                        }
                        ++cnt
                    }
                }
                sbNewText.append("\n")
            }
            //把结尾多余的\n去掉
            if (!rawText.endsWith("\n")) {
                sbNewText.deleteCharAt(sbNewText.length - 1)
            }
            text = sbNewText.toString()
        }
    }
}