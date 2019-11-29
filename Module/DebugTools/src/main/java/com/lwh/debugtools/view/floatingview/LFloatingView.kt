package com.lwh.debugtools.view.floatingview

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.lwh.debugtools.R

/**
 * @author lwh
 * @Date 2019/11/21 17:58
 * @description 悬浮窗
 */
class LFloatingView(context: Context) : FloatingMagnetView(context, null) {

    private val mIcon: ImageView

    init {
        View.inflate(context, R.layout.l_floating_view, this)
        mIcon = findViewById(R.id.icon)
    }

    fun setIconImage(@DrawableRes resId: Int) {
        mIcon.setImageResource(resId)
    }

}
