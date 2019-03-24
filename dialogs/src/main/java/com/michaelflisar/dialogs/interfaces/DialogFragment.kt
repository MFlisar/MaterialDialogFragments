package com.michaelflisar.dialogs.interfaces

import androidx.fragment.app.FragmentActivity

interface DialogFragment {
    fun show(activity: FragmentActivity, tag: String = this::class.java.name)
}