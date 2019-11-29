package com.lwh.debugtools.view.statusview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.lwh.debugtools.R
import com.lwh.debugtools.view.statusview.StatusUtils.getStatusBarSize

/**
 * @author lwh
 * @Date 2019/11/25 10:50
 * @description 状态栏
 */
class StatusBarView : View {

    var statusBarType = StatusBarType.VERTICAL

    /**
     * 折叠状态
     */
    private var foldStatus: FoldStatus = FoldStatus.COLLAPSE

    /**
     * 状态栏真实高度
     */
    private val statusHeight: Int = getStatusBarSize()

    /**
     * view大小
     */
    private var viewSize: Int = 0

    /**
     * 动画时间
     */
    var animationDuration: Long = 300

    enum class FoldStatus(val value: Int) {
        /**
         * 展开
         */
        EXPAND(0x000000),
        /**
         * 折叠
         */
        COLLAPSE(0x000001)
    }

    enum class StatusBarType(val value: Int) {
        //竖屏状态栏
        VERTICAL(0x000000),
        //横屏状态栏
        HORIZONTAL(0x000001),
        //横竖屏状态栏
        ALL(0x000002)

    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.StatusBarView)
            statusBarType =
                when (typedArray.getInt(R.styleable.StatusBarView_statusBarType, StatusBarType.VERTICAL.value)) {
                    0x000000 -> StatusBarType.VERTICAL
                    0x000001 -> StatusBarType.HORIZONTAL
                    0x000002 -> StatusBarType.ALL
                    else -> StatusBarType.VERTICAL
                }
            foldStatus =
                when (typedArray.getInt(R.styleable.StatusBarView_foldStatus, FoldStatus.EXPAND.value)) {
                    0x000000 -> FoldStatus.EXPAND
                    0x000001 -> FoldStatus.COLLAPSE
                    else -> FoldStatus.EXPAND
                }
        }


        initExpand(foldStatus)
    }

    private fun initExpand(foldStatus: FoldStatus) {
        this.foldStatus = foldStatus
        if (foldStatus == FoldStatus.EXPAND) {
            postDelayed({ animateToggle(10) }, 50)
        }
    }


    /**
     * 切换动画实现
     */
    private fun animateToggle(animationDuration: Long) {

        val orientation = resources.configuration.orientation
        val portrait = orientation == Configuration.ORIENTATION_PORTRAIT
        val value: Float = statusHeight.toFloat()

        val animation = if (foldStatus == FoldStatus.EXPAND)
            ValueAnimator.ofFloat(0f, value)
        else
            ValueAnimator.ofFloat(value, 0f)
        animation.duration = animationDuration / 2
        animation.startDelay = animationDuration / 2
        animation.addUpdateListener {
            val value = animation.animatedValue as Float
            Log.i("TAG", "value:$value")
            refreshLayout(value.toInt())
        }
        animation.start()
    }

    private fun setViewHeight(view: View, height: Int) {
        val params = view.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = height
        viewSize = height
        view.requestLayout()
    }

    private fun setViewWidth(view: View, width: Int) {
        val params = view.layoutParams
        params.width = width
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewSize = width
        view.requestLayout()
    }

    /**
     * 刷新layout
     */
    fun refreshLayout() {
        refreshLayout(viewSize)
    }

    /**
     * 刷新layout
     */
    private fun refreshLayout(value:Int) {
        val orientation = resources.configuration.orientation
        val portrait = orientation == Configuration.ORIENTATION_PORTRAIT
        if (portrait) {
            setViewHeight(this@StatusBarView, value)
        } else {
            if (statusBarType == StatusBarType.VERTICAL) {
                setViewWidth(this@StatusBarView, value)
            } else {
                setViewHeight(this@StatusBarView, value)
            }
        }
    }

    /**
     * 折叠view
     */
    fun collapse() {
        if (foldStatus != FoldStatus.COLLAPSE) {
            foldStatus = FoldStatus.COLLAPSE
            animateToggle(animationDuration)
        }
    }

    /**
     * 展开view
     */
    fun expand() {
        if (foldStatus != FoldStatus.EXPAND) {
            foldStatus = FoldStatus.EXPAND
            animateToggle(animationDuration)
        }
    }

    fun toggleExpand() {
        if (foldStatus == FoldStatus.EXPAND) {
            collapse()
        } else {
            expand()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        var height = 0
//        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
//        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
//        when (heightSpecMode) {
//            View.MeasureSpec.UNSPECIFIED -> {
//                height = heightSpecSize
//            }
//            View.MeasureSpec.AT_MOST -> {//wrap_content时候
//                height = 0
//            }
//            View.MeasureSpec.EXACTLY -> {
//                //当xml布局中是准确的值，比如200dp是，判断一下当前view的宽度和准确值,取两个中大的，这样的好处是当view的宽度本事超过准确值不会出界
//                //其实可以直接使用准确值
//                height = heightSpecSize
//            }
//        }
        val orientation = resources.configuration.orientation
        val portrait = orientation == Configuration.ORIENTATION_PORTRAIT
        if (!portrait && statusBarType == StatusBarType.VERTICAL) {
            //横屏
            setMeasuredDimension(viewSize, MeasureSpec.getSize(heightMeasureSpec))
        } else {
            //竖屏
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), viewSize)
        }
    }

}