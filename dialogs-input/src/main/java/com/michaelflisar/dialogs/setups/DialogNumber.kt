package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogEventImpl
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.events.MaterialDialogEvent
import com.michaelflisar.dialogs.fragments.DialogNumberFragment
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize


@Parcelize
class DialogNumber(
    // base setup
    override val id: Int,
    override val title: Text?,
    val initialValue: Int? = null,
    val text: Text? = null,
    val hint: Text? = null,
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
    val selectText: Boolean = false,
    val errorMessage: Text? = null
) : BaseDialogSetup {

    override fun create() = DialogNumberFragment.create(this)

    sealed class Event {
        class Empty(setup: BaseDialogSetup, button: MaterialDialogButton) : Event(),
            MaterialDialogEvent by MaterialDialogEventImpl(setup, button)

        class Data(setup: BaseDialogSetup, button: MaterialDialogButton, val values: List<Int>) : Event(),
            MaterialDialogEvent by MaterialDialogEventImpl(setup, button) {
            constructor(setup: BaseDialogSetup, button: MaterialDialogButton, value: Int) : this(
                setup,
                button,
                arrayListOf(value)
            )
            val value: Int = values[0]
            fun getValueAt(index: Int = 0) = if (values.size > index) values[index] else null
        }
    }
}