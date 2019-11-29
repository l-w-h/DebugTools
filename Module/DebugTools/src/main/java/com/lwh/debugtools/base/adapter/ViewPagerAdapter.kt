package com.lwh.debugtools.base.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author lwh
 * @Date 2019/10/19 17:05
 * @description ViewPagerAdapter
 */
class ViewPagerAdapter(fm: FragmentManager, private val titles:Array<String>?, private val fragments:List<Fragment>) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titles == null){
            ""
        }else {
            titles[position]
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }
}