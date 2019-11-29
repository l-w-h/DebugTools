package com.lwh.debugtools.view.floatingview

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes

/**
 * @author lwh
 * @Date 2019/11/21 18:08
 * @description 悬浮窗管理器
 */
interface IFloatingView {

    fun remove(): FloatingView

    fun add(): FloatingView

    fun attach(activity: Activity): FloatingView

    fun attach(container: FrameLayout?): FloatingView

    fun detach(activity: Activity): FloatingView

    fun detach(container: FrameLayout?): FloatingView

    fun getView(): LFloatingView?

    fun icon(@DrawableRes resId: Int): FloatingView

    fun layoutParams(params: ViewGroup.LayoutParams): FloatingView

    fun listener(magnetViewListener: MagnetViewListener): FloatingView

}
