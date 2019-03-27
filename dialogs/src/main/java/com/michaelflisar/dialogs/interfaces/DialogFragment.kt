package com.michaelflisar.dialogs.interfaces

import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.enums.SendResultType

interface DialogFragment {
    fun <T : BaseDialogSetup> getSetup(): T
    fun show(activity: FragmentActivity, sendResultType: SendResultType? = null, tag: String = this::class.java.name)

    fun setSetupArgs(setup: BaseDialogSetup)
}