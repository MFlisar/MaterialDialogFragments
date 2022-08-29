package com.michaelflisar.dialogs.internal.managers

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.ViewData

internal abstract class BaseDialogManager<S : MaterialDialogSetup<S, F>, F : MaterialDialogFragment<F, S, *>>(
    val fragment: F
) {
    open fun onCreate(savedInstanceState: Bundle?) {}
    open fun onStart() {}
    open fun onSaveInstanceState(outState: Bundle) {}

    interface ICustomDismiss {
        fun dismiss()
        fun dismissAllowingStateLoss()
    }

    interface IDialog {
        fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    }

    interface IView {
        fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View?

        fun onViewCreated(view: View, savedInstanceState: Bundle?): ViewData
    }
}