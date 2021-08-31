package com.michaelflisar.dialogs.classes

import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.parcelize.Parcelize

sealed class DialogStyle : Parcelable {

    @Parcelize
    object Dialog : DialogStyle()

    @Parcelize
    class BottomSheet(
            val peekHeight: Int? = null,
            val resPeekHeight: Int? = null,
            val layoutModeMatchParent: Boolean = false,
            val initialState: Int = BottomSheetBehavior.STATE_HALF_EXPANDED
    ) : DialogStyle()
}