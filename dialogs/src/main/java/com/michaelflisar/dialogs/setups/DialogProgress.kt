package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogProgressFragment
import com.michaelflisar.dialogs.interfaces.IProgressDialogFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogProgress constructor(
        // base setup
        override val id: Int,
        override val title: Text,
        val text: Text?,
        val horizontal: Boolean = true,
        val dismissOnNegative: Boolean = false,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = false,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val style: DialogStyle = DialogStyle.Dialog
        ) : BaseDialogSetup {

    override fun create() = DialogProgressFragment.create(this)

    companion object {
        private var dialog: IProgressDialogFragment? = null

        fun close() = close(false)

        private fun close(forcedByNewDialog: Boolean) {
            try {
                dialog?.close(forcedByNewDialog)
            } catch (e: NullPointerException) {
                // ignore...
            }
            dialog = null
        }

        fun update(text: Text) {
            dialog?.update(text)
        }

        fun setDialog(dialog: IProgressDialogFragment) {
            close(true)
            Companion.dialog = dialog
        }
    }
}