package com.michaelflisar.dialogs.setups

import android.os.Bundle
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogNumberPickerFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogNumberPicker(
        // base setup
        override val id: Int,
        override val title: Text,
        val initialValue: Int = 0,
        val text: Text? = null,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val darkTheme: Boolean = false,
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,

        // special setup
        val min: Int? = null,
        val max: Int? = null,
        val step: Int = 1,
        val valueFormatRes: Int? = null,
        val additonalValues: ArrayList<NumberField> = arrayListOf()
) : BaseDialogSetup {

    override fun create(): DialogFragment = DialogNumberPickerFragment.create(this)

    @Parcelize
    class NumberField(val label: Text?, val initialValue: Int) : Parcelable

}