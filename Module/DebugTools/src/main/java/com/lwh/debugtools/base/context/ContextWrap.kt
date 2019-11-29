package com.lwh.debugtools.base.context

import android.app.Activity
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * @author lwh
 * @Date 2019/10/19 15:39
 * @description ContextWrap
 */
class ContextWrap private constructor(activity: Activity?) {
    private val activity: WeakReference<Activity> = WeakReference<Activity>(activity)
    private var fragment: WeakReference<Fragment>? = null

    private constructor(fragment: Fragment) : this(fragment.activity) {
        this.fragment = WeakReference(fragment)
    }

    fun getActivity(): Activity {
        return activity.get()!!
    }

    fun getFragment(): Fragment? {
        return fragment?.get()
    }

    fun onDestroy() {
        activity.clear()
        fragment?.clear()
        fragment = null
    }

    companion object {

        fun of(activity: Activity): ContextWrap {
            return ContextWrap(activity)
        }

        fun of(fragment: Fragment): ContextWrap {
            return ContextWrap(fragment)
        }
    }
}
