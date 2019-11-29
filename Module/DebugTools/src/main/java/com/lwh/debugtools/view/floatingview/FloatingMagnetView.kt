package com.lwh.debugtools.view.floatingview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

import com.lwh.debugtools.view.floatingview.utils.SystemUtils

/**
 * @author lwh
 * @Date 2019/11/21 18:00
 * @description 磁力吸附悬浮窗
 */
open class FloatingMagnetView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var mOriginalRawX: Float = 0.toFloat()
    private var mOriginalRawY: Float = 0.toFloat()
    private var mOriginalX: Float = 0.toFloat()
    private var mOriginalY: Float = 0.toFloat()
    private var mMagnetViewListener: MagnetViewListener? = null
    private var mLastTouchDownTime: Long = 0
    protected lateinit var mMoveAnimator: MoveAnimator
    protected var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mStatusBarHeight: Int = 0

    protected val isOnClickEvent: Boolean
        get() = System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD

    protected val isNearestLeft: Boolean
        get() {
            val middle = mScreenWidth / 2
            return x < middle
        }

    fun setMagnetViewListener(magnetViewListener: MagnetViewListener) {
        this.mMagnetViewListener = magnetViewListener
    }

    init {
        init()
    }

    private fun init() {
        mMoveAnimator = MoveAnimator()
        mStatusBarHeight = SystemUtils.getStatusBarHeight(context)
        isClickable = true
        updateSize()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                changeOriginalTouchParams(event)
                updateSize()
                mMoveAnimator.stop()
            }
            MotionEvent.ACTION_MOVE -> updateViewPosition(event)
            MotionEvent.ACTION_UP -> {
                moveToEdge()
                if (isOnClickEvent) {
                    dealClickEvent()
                }
            }
        }
        return true
    }

    protected fun dealClickEvent() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener!!.onClick(this)
        }
    }

    private fun updateViewPosition(event: MotionEvent) {
        x = mOriginalX + event.rawX - mOriginalRawX
        // 限制不可超出屏幕高度
        var desY = mOriginalY + event.rawY - mOriginalRawY
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight.toFloat()
        }
        if (desY > mScreenHeight - (height + height / 2)) {
            desY = (mScreenHeight - (height + height / 2)).toFloat()
        }
        y = desY
    }

    private fun changeOriginalTouchParams(event: MotionEvent) {
        mOriginalX = x
        mOriginalY = y
        mOriginalRawX = event.rawX
        mOriginalRawY = event.rawY
        mLastTouchDownTime = System.currentTimeMillis()
    }

    protected fun updateSize() {
        mScreenWidth = SystemUtils.getScreenWidth(context) - this.width
        mScreenHeight = SystemUtils.getScreenHeight(context)
    }

    fun moveToEdge() {
        val moveDistance = (if (isNearestLeft) MARGIN_EDGE else mScreenWidth - MARGIN_EDGE).toFloat()
        mMoveAnimator.start(moveDistance, y)
    }

    fun onRemove() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener!!.onRemove(this)
        }
    }

    protected inner class MoveAnimator : Runnable {

        private val handler = Handler(Looper.getMainLooper())
        private var destinationX: Float = 0.toFloat()
        private var destinationY: Float = 0.toFloat()
        private var startingTime: Long = 0

        internal fun start(x: Float, y: Float) {
            this.destinationX = x
            this.destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView == null || rootView.parent == null) {
                return
            }
            val progress = Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
            val deltaX = (destinationX - x) * progress
            val deltaY = (destinationY - y) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }

        internal fun stop() {
            handler.removeCallbacks(this)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x = x + deltaX
        y = y + deltaY
    }

    companion object {

        val MARGIN_EDGE = 13
        private val TOUCH_TIME_THRESHOLD = 100
    }

}
