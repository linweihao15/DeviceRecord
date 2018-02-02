package me.jack.devicerecord.util

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.support.annotation.Nullable
import me.jack.devicerecord.R
import org.apache.commons.lang3.ClassUtils
import java.util.*

/**
 * Created by Jack on 2017/12/1.
 */
class FragmentUtils private constructor() {

    companion object {
        val instance by lazy { FragmentUtils() }
    }

    private var curTag = ""
    private var lastTag = ""
    private var stack = LinkedList<String>()

    fun show(activity: Activity, tag: String, bundle: Bundle) {
        val ft = activity.fragmentManager.beginTransaction()
        val f = getFragment(activity, tag, bundle)
        if (curTag.isEmpty()) {
            ft.add(R.id.container, f, tag)
        } else {
            ft.setCustomAnimations(R.animator.fg_right_enter, R.animator.fg_left_exit)
                    .replace(R.id.container, f, tag)
        }
        ft.commit()
        lastTag = curTag
        curTag = tag
        if (lastTag.isNotEmpty()) {
            stack.offerLast(lastTag)
        }
    }

    fun back(activity: Activity, bundle: Bundle) {
        if (stack.isNotEmpty()) {
            val tag = stack.pollLast()
            val ft = activity.fragmentManager.beginTransaction()
            val f = getFragment(activity, tag, bundle)
            ft.setCustomAnimations(R.animator.fg_left_enter, R.animator.fg_right_exit)
                    .replace(R.id.container, f, tag)
                    .commit()
            curTag = tag
        }
    }

    fun currentFragment(activity: Activity): Fragment {
        return findFragmentByTag(activity, curTag)
    }

    @Nullable
    private fun findFragmentByTag(activity: Activity, tag: String) = activity.fragmentManager.findFragmentByTag(tag)

    private fun getFragment(activity: Activity, tag: String, bundle: Bundle): Fragment {
        var f = findFragmentByTag(activity, tag)
        if (f == null) {
            try {
                f = ClassUtils.getClass(tag).newInstance() as Fragment
                f.arguments = bundle
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        } else {
            f.arguments.putAll(bundle)
        }
        return f
    }


}