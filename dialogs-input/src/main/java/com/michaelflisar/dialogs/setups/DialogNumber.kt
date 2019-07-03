package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize


@Parcelize
class DialogNumber(
        // base setup
        override val id: Int,
        override val title: Text,
        val initialValue: Int? = null,
        val text: Text? = null,
        val hint: Text? = null,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,

        // special setup
        val min: Int? = null,
        val max: Int? = null,
        val errorMessage: Text? = null
) : BaseDialogSetup {
    override fun create(): DialogFragment<DialogNumber> = com.michaelflisar.dialogs.fragments.DialogNumberFragment.create(this)
}