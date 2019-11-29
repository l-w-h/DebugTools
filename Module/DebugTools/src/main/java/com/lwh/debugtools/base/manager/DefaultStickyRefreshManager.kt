package com.lwh.debugtools.base.manager

import android.view.View
import com.lwh.debugtools.R
import com.lwh.debugtools.base.diff.BaseDiffCallBack
import com.lwh.debugtools.base.ui.BaseActivity
import com.lwh.debugtools.base.ui.BaseFragment
import com.lwh.debugtools.base.view.framelayout.StickyHeaderLayout

/**
 * @author lwh
 * @Date 2019/10/21 15:42
 * @description 默认吸顶+刷新布局管理器
 */
class DefaultStickyRefreshManager<DiffCallBack : BaseDiffCallBack> :
    DefaultRefreshManager<DiffCallBack> {

    /**
     * 吸顶布局
     */
    private var stickyHeaderLayout: StickyHeaderLayout? = null
    /**
     * 是否吸顶。 默认：true
     */
    var isSticky: Boolean = true
        set(value) {
            field = value
            stickyHeaderLayout?.setSticky(field)
        }

    constructor(activity: BaseActivity, rootView: View, bodyLayout: Int, loadData: () -> Unit) : super(
        activity,
        rootView,
        bodyLayout,
        loadData
    )

    constructor(fragment: BaseFragment, rootView: View, bodyLayout: Int, loadData: () -> Unit) : super(
        fragment,
        rootView,
        bodyLayout,
        loadData
    )


    override fun init(context: Any, rootView: View) {
        super.init(context, rootView)
        bodyView?.let {
            stickyHeaderLayout = it.findViewById(R.id.stickyHeaderLayout)
        }
    }

}