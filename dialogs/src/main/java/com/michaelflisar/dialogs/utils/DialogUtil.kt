package com.michaelflisar.dialogs.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback

/**
 * Created by Michael on 14.05.2017.
 */

object DialogUtil {

    fun trySendResult(
        event: BaseDialogEvent,
        fragment: Fragment,
        sendResultType: SendResultType = DialogSetup.DEFAULT_SEND_RESULT_TYPE
    ) {
        var stopAfterFirstHandled = true
        val allCallbacks = when (sendResultType) {
            is SendResultType.All -> {
                stopAfterFirstHandled = sendResultType.stopAfterFirstHandled
                getAllCallbackHandlers(fragment)
            }
            is SendResultType.ParentFragment -> convertFragmentToCallbackList(fragment.parentFragment)
            is SendResultType.TargetFragment -> convertFragmentToCallbackList(fragment.targetFragment)
            is SendResultType.Fragment -> convertFragmentToCallbackList(fragment)
            is SendResultType.Activity -> convertActivityToCallbackList(fragment.activity)
            is SendResultType.First -> {
                sendResultType.priority.map {
                    when (it) {
                        SendResultType.First.FirstType.Activity -> convertActivityToCallbackList(fragment.activity).firstOrNull()
                        SendResultType.First.FirstType.ParentFragment -> convertFragmentToCallbackList(fragment.parentFragment).firstOrNull()
                        SendResultType.First.FirstType.TargetFragment -> convertFragmentToCallbackList(fragment.targetFragment).firstOrNull()
                        SendResultType.First.FirstType.Fragment -> convertFragmentToCallbackList(fragment).firstOrNull()
                    }
                }
            }
            is SendResultType.Manual -> ArrayList<DialogFragmentCallback>()
        }
            .filterNotNull()

        for (c in allCallbacks) {
            val handled = c.onDialogResultAvailable(event)
            if (handled && stopAfterFirstHandled) {
                break
            }
        }
    }

    // ----------------
    // helper functions
    // ----------------

    private fun getFragments(activity: FragmentActivity): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.addAll(activity.supportFragmentManager.fragments)
        fragments.addAll(getSubFragments(activity.supportFragmentManager.fragments))
        return fragments
    }

    private fun getSubFragments(fragments: List<Fragment>): ArrayList<Fragment> {
        val result = ArrayList<Fragment>()
        for (f in fragments) {
            result.addAll(f.childFragmentManager.fragments)
            result.addAll(getSubFragments(f.childFragmentManager.fragments))
        }
        return result
    }

    private fun getAllCallbackHandlers(fragment: Fragment): ArrayList<DialogFragmentCallback> {
        val result = ArrayList<DialogFragmentCallback>()
        (fragment.activity as? DialogFragmentCallback)?.let {
            result.add(it)
        }
        // 2) we recursively get all fragments of the activity and add them to the callbacks list
        fragment.activity?.let {
            result.addAll(getFragments(it)
                .filter { it is DialogFragmentCallback }
                .map { it as DialogFragmentCallback }
            )
        }
        return result
    }

    private fun convertActivityToCallbackList(activity: Activity?): ArrayList<DialogFragmentCallback> {
        return (activity as? DialogFragmentCallback)?.let { ArrayList<DialogFragmentCallback>().apply { add(it) } }
            ?: ArrayList()
    }

    private fun convertFragmentToCallbackList(fragment: Fragment?): ArrayList<DialogFragmentCallback> {
        return (fragment as? DialogFragmentCallback)?.let { ArrayList<DialogFragmentCallback>().apply { add(it) } }
            ?: ArrayList()
    }
}
