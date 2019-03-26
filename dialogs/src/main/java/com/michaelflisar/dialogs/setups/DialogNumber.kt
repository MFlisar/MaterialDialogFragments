package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogNumberFragment
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
        override val darkTheme: Boolean = false,
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,

        // special setup
        val min: Int? = null,
        val max: Int? = null,
        val errorMessage: Text? = null
) : BaseDialogSetup {
    override fun create(): DialogFragment = DialogNumberFragment.create(this)
}