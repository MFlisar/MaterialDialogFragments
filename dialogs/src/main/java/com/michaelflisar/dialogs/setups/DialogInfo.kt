package com.michaelflisar.dialogs.setups

import android.graphics.Color
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogInfoFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize

/**
 * [textIsHtml] - define if the privided label is formatted as html label or not
 *
 * [timerPosButton] - if value > 0, the positive button will only be enabled after the provided value in seconds is over
 *
 * [warning] - provide an additional label, that will be appended after the main label and which color can be adjusted
 *
 * [warningColor] - define a custom color for the warning label
 *
 * [warningSeparator] - define a custom separator that will be used between label and warning
 *
 * [warningTextSizeFactor] - define a custom label size factor for the warning message
 */
@Parcelize
class DialogInfo(
        // base setup
        override val id: Int,
        override val title: Text,
        val text: Text,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val darkTheme: Boolean = false,
        override val negButton: Text? = null,
        override val neutrButton: Text?  = null,
        override val cancelable: Boolean = true,

        // special setup
        val textIsHtml: Boolean = false,
        val timerPosButton: Int = 0,
        val warning: Text? = null,
        val warningColor: Int = Color.RED,
        val warningSeparator: String = "\n",
        val warningTextSizeFactor: Float = 1f
) : BaseDialogSetup {

    override fun create(): DialogFragment = DialogInfoFragment.create(this)

}