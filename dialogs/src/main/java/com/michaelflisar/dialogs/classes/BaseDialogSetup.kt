package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.interfaces.DialogFragment

interface BaseDialogSetup : Parcelable {
    val id: Int
    val title: Text
    val posButton: Text
    val darkTheme: Boolean
    val negButton: Text?
    val neutrButton: Text?
    val cancelable: Boolean

    fun create(): DialogFragment
}