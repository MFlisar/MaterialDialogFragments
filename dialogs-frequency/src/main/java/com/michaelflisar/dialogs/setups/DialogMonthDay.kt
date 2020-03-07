package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MonthDay
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogMonthDayFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class DialogMonthDay(
    // base setup
        override val id: Int,
        override val title: Text? = null,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val style: DialogStyle = DialogStyle.Dialog,

    // special setup
        val monthDay: MonthDay

) : BaseDialogSetup {

    override fun create() = DialogMonthDayFragment.create(this)
}