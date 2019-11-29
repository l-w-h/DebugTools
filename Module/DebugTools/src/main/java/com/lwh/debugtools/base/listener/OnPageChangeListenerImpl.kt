package com.lwh.debugtools.base.listener

import androidx.viewpager.widget.ViewPager

/**
 * @author lwh
 * @Date 2019/10/19 17:49
 * @description OnPageChangeListenerImpl
 */
open interface OnPageChangeListenerImpl: ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }
}
