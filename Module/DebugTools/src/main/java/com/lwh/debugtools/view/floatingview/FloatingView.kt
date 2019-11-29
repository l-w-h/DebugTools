package com.lwh.debugtools.view.floatingview

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import com.lwh.debugtools.R
import com.lwh.debugtools.view.floatingview.utils.LContext


/**
 * @author lwh
 * @Date 2019/11/21 18:03
 * @description 悬浮窗管理器
 */
class FloatingView private constructor() : IFloatingView {

    private var mLFloatingView: LFloatingView? = null
    private var mContainer: FrameLayout? = null

    private val params: FrameLayout.LayoutParams
        get() {
            val params = FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.BOTTOM or Gravity.END
            params.setMargins(params.leftMargin, params.topMargin, 13, LContext.get().resources.getDimensionPixelSize(R.dimen.dp_56))
            return params
        }

    override fun remove(): FloatingView {
        Handler(Looper.getMainLooper()).post(Runnable {
            if (mLFloatingView == null) {
                return@Runnable
            }
            if (ViewCompat.isAttachedToWindow(mLFloatingView!!) && mContainer != null) {
                mContainer!!.removeView(mLFloatingView)
            }
            mLFloatingView = null
        })
        return this
    }


    private fun ensureMiniPlayer(context: Context) {
        synchronized(this) {
            if (mLFloatingView != null) {
                return
            }
            mLFloatingView = LFloatingView(context.applicationContext)
            mLFloatingView!!.layoutParams = params
            addViewToWindow(mLFloatingView)
        }
    }

    override fun add(): FloatingView {
        ensureMiniPlayer(LContext.get())
        return this
    }

    override fun attach(activity: Activity): FloatingView {
        attach(getActivityRoot(activity))
        return this
    }

    override fun attach(container: FrameLayout?): FloatingView {
        if (container == null || mLFloatingView == null) {
            mContainer = container
            return this
        }
        if (mLFloatingView!!.parent === container) {
            return this
        }
        if (mContainer != null && mLFloatingView!!.parent === mContainer) {
            mContainer!!.removeView(mLFloatingView)
        }else if (mLFloatingView!!.parent != null){
            (mLFloatingView!!.parent as ViewGroup).removeView(mLFloatingView)
        }
        mContainer = container
        container.addView(mLFloatingView)
        return this
    }

    override fun detach(activity: Activity): FloatingView {
        detach(getActivityRoot(activity))
        return this
    }

    override fun detach(container: FrameLayout?): FloatingView {
        if (mLFloatingView != null && container != null && ViewCompat.isAttachedToWindow(mLFloatingView!!)) {
            container.removeView(mLFloatingView)
        }
        if (mContainer === container) {
            mContainer = null
        }
        return this
    }

    override fun getView(): LFloatingView? {
        return mLFloatingView
    }

    override fun icon(@DrawableRes resId: Int): FloatingView {
        if (mLFloatingView != null) {
            mLFloatingView!!.setIconImage(resId)
        }
        return this
    }

    override fun layoutParams(params: ViewGroup.LayoutParams): FloatingView {
        if (mLFloatingView != null) {
            mLFloatingView!!.layoutParams = params
        }
        return this
    }

    override fun listener(magnetViewListener: MagnetViewListener): FloatingView {
        if (mLFloatingView != null) {
            mLFloatingView!!.setMagnetViewListener(magnetViewListener)
        }
        return this
    }

    private fun addViewToWindow(view: LFloatingView?) {
        if (mContainer == null) {
            return
        }
        mContainer!!.addView(view)
    }

    private fun getActivityRoot(activity: Activity?): FrameLayout? {
        if (activity == null) {
            return null
        }
        try {
            return activity.window.decorView.findViewById(android.R.id.content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    companion object {
        @Volatile
        private var mInstance: FloatingView? = null

        fun get(): FloatingView {
            if (mInstance == null) {
                synchronized(FloatingView::class.java) {
                    if (mInstance == null) {
                        mInstance = FloatingView()
                    }
                }
            }
            return mInstance!!
        }
    }
}