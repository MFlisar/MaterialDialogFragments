package com.michaelflisar.dialogs.setups

import android.R
import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.text.Text
import com.michaelflisar.dialogs.fragments.DialogColorFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogColor(
        // base setup
        override val id: Int,
        override val title: Text,
        val color: Int,
        val showAlpha: Boolean = false,
        val moveToCustomPageOnPickerSelection: Boolean = false,
        val updateCustomColorOnPickerSelection: Boolean = true,
        override val style: DialogStyle = DialogStyle.Dialog,

        // special setup
        override val posButton: Text = Text.Resource(R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text?  = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT
) : BaseDialogSetup {

    override fun create() = DialogColorFragment.create(this)

}