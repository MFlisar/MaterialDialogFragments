package com.michaelflisar.dialogs.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback

/**
 * Created by Michael on 14.05.2017.
 */

object DialogUtil {
    fun trySendResult(event: BaseDialogEvent, fragment: Fragment, sendResultType: SendResultType) {

        val callbacks: ArrayList<DialogFragmentCallback> = ArrayList()

        val callback = when (sendResultType) {
            SendResultType.FullHierarchy -> {
                // 1) we add the activity if appropriate
                (fragment.activity as? DialogFragmentCallback)?.let {
                    callbacks.add(it)
                }
                // 2) we recursively get all fragments of the activity and add them to the callbacks list
                fragment.activity?.let {
                    callbacks.addAll(getFragments(it)
                            .filter { it is DialogFragmentCallback }
                            .map { it as DialogFragmentCallback }
                    )
                }
                null
            }
            SendResultType.ParentFragmentOnly -> {
                fragment.parentFragment as? DialogFragmentCallback
            }
            SendResultType.TargetFragmentOnly -> {
                fragment.targetFragment as? DialogFragmentCallback
            }
            SendResultType.ActivityOnly -> {
                fragment.activity  as? DialogFragmentCallback
            }
            SendResultType.TargetOrParentOrActivity -> {
                if (fragment.targetFragment != null)
                    fragment.targetFragment as? DialogFragmentCallback
                else if (fragment.parentFragment != null)
                    fragment.parentFragment as? DialogFragmentCallback
                else
                    fragment.activity as? DialogFragmentCallback
            }
        }

        callback?.let {
            callbacks.add(it)
        }

        for (c in callbacks) {
            c.onDialogResultAvailable(event)
        }
    }

    fun getFragments(activity: FragmentActivity): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.addAll(activity.supportFragmentManager.fragments)
        fragments.addAll(getSubFragments(activity.supportFragmentManager.fragments))
        return fragments
    }

    fun getSubFragments(fragments: List<Fragment>): ArrayList<Fragment> {
        val result = ArrayList<Fragment>()
        for (f in fragments) {
            result.addAll(f.childFragmentManager.fragments)
            result.addAll(getSubFragments(f.childFragmentManager.fragments))
        }
        return result
    }
}
