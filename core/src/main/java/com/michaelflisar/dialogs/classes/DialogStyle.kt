package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.parcelize.Parcelize

sealed class DialogStyle : Parcelable {

    @Parcelize
    object Dialog : DialogStyle()

    @Parcelize
    class BottomSheet(
        val initialState: Int = BottomSheetBehavior.STATE_HALF_EXPANDED
    ) : DialogStyle()

    @Parcelize
    object FullScreen: DialogStyle()
}