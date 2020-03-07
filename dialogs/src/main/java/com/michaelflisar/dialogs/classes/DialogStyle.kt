package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class DialogStyle : Parcelable {

    @Parcelize
    object Dialog : DialogStyle()

    @Parcelize
    class BottomSheet(
            val peekHeight: Int? = null,
            val resPeekHeight: Int? = null,
            val layoutModeMatchParent: Boolean = false
    ) : DialogStyle()
}