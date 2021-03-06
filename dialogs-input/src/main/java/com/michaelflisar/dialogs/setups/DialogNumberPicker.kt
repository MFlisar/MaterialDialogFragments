package com.michaelflisar.dialogs.setups

import android.os.Bundle
import android.os.Parcelable
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.text.Text
import com.michaelflisar.dialogs.fragments.DialogNumberPickerFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogNumberPicker(
        // base setup
        override val id: Int,
        override val title: Text?,
        val initialValue: Int = 0,
        val text: Text? = null,
        override val posButton: Text = Text.Resource(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val style: DialogStyle = DialogStyle.Dialog,

        // special setup
        val min: Int? = null,
        val max: Int? = null,
        val step: Int = 1,
        val valueFormatRes: Int? = null,
        val additonalValues: ArrayList<NumberField> = arrayListOf()
) : BaseDialogSetup {

    override fun create() = DialogNumberPickerFragment.create(this)

    @Parcelize
    class NumberField(val label: Text?, val initialValue: Int) : Parcelable

}