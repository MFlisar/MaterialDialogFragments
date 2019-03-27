package com.michaelflisar.dialogs.setups

import android.R
import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogColorFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogColor(
        // base setup
        override val id: Int,
        override val title: Text,
        val selectedColorGroupIndex: Int? = null,
        val color: Int,
        val darkTheme: Boolean? = null,

        override val posButton: Text = Text.TextRes(R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text?  = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null

        // special setup

) : BaseDialogSetup {

    override fun create(): DialogFragment = DialogColorFragment.create(this)

    fun useDarkTheme() = darkTheme ?: DialogSetup.useDarkTheme()

}