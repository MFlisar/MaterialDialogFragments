package com.michaelflisar.dialogs.interfaces

import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.enums.SendResultType

interface DialogFragment {
    fun show(activity: FragmentActivity, sendResultType: SendResultType? = null, tag: String = this::class.java.name)
}