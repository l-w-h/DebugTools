package com.lwh.debugtools.base.view.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.lwh.debugtools.R

const val DEFAULT_FONT_PATH = "iconfont/DT_iconfont.ttf"

/**
 * @author lwh
 * @Date 2019/10/22 12:27
 * @description iconFont字体图标
 */
class IconTextView : AppCompatCheckedTextView {

    private var fontPath: String? = null
        set(value) {
            field = value
            val typeface: Typeface = Typeface.createFromAsset(context.assets, fontPath)
            setTypeface(typeface)
        }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        var font: String? = null
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.IconTextView)
            font = typedArray.getString(R.styleable.IconTextView_fontPath)
            typedArray.recycle()
        }
        fontPath = font ?: DEFAULT_FONT_PATH
    }


}