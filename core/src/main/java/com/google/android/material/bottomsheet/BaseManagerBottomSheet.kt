package com.google.android.material.bottomsheet

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.internal.managers.BaseDialogManager
import com.michaelflisar.dialogs.MaterialDialogSetup

// written based on com.google.android.material.bottomsheet.BottomSheetDialogFragment
internal abstract class BaseManagerBottomSheet<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, B>, B : ViewBinding>(fragment: F) :
    BaseDialogManager<S, F>(fragment), BaseDialogManager.ICustomDismiss, BaseDialogManager.IDialog {

    private var waitingForDismissAllowingStateLoss = false

    private var restoredState: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        restoredState = savedInstanceState?.getInt("STATE_BOTTOM_SHEET")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val state = getBehaviour()?.state
        state?.let {
            outState.putInt("STATE_BOTTOM_SHEET", it)
        }
    }

    override fun onStart() {
        getBehaviour()?.let {
            // setting state to collapsed fixes positioning problems on screen rotation...
            it.state = BottomSheetBehavior.STATE_COLLAPSED
            fragment.requireView().post {
                it.state = restoredState ?: (fragment.setup.style as DialogStyle.BottomSheet).initialState
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MyBottomSheetDialog(fragment.requireContext(), fragment.theme)
    }

    override fun dismiss() {
        if (!tryDismissWithAnimation(false)) {
            fragment.superDismiss()
        }
    }

    override fun dismissAllowingStateLoss() {
        if (!tryDismissWithAnimation(true)) {
            fragment.superDismissAllowingStateLoss()
        }
    }

    // ------------------
    // protected helper functions
    // ------------------

    protected fun tryDismissWithAnimation(allowingStateLoss: Boolean): Boolean {
        val baseDialog = getBottomSheetDialog()
        if (baseDialog?.behavior != null && baseDialog.behavior.isHideable && baseDialog.dismissWithAnimation) {
            dismissWithAnimation(baseDialog.behavior, allowingStateLoss)
            return true
        }
        return false
    }

    protected fun dismissWithAnimation(
        behavior: BottomSheetBehavior<*>, allowingStateLoss: Boolean
    ) {
        waitingForDismissAllowingStateLoss = allowingStateLoss
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            dismissAfterAnimation()
        } else {
            if (fragment.dialog is BottomSheetDialog) {
                (fragment.dialog as BottomSheetDialog).removeDefaultCallback()
            }
            behavior.addBottomSheetCallback(BottomSheetDismissCallback(this))
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    protected fun dismissAfterAnimation() {
        if (waitingForDismissAllowingStateLoss) {
            fragment.dismissAllowingStateLoss()
        } else {
            fragment.dismiss()
        }
    }

    protected class BottomSheetDismissCallback<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, B>, B : ViewBinding>(val manager: BaseManagerBottomSheet<S, F, B>) :
        BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                manager.dismissAfterAnimation()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    protected fun getBottomSheetDialog(): BottomSheetDialog? {
        val baseDialog = fragment.dialog
        if (baseDialog is BottomSheetDialog) {
            return baseDialog
        }
        return null
    }

    protected fun getBehaviour(): BottomSheetBehavior<*>? {
        val baseDialog = fragment.dialog
        if (baseDialog is BottomSheetDialog) {
            return baseDialog.behavior
        }
        return null
    }
}