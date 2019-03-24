package com.michaelflisar.dialogs.setups

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogProgressFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.interfaces.IProgressDialogFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogProgress private constructor(
        // base setup
        override val id: Int,
        override val title: Text,
        val text: Text?,
        val horizontal: Boolean,
        val dismissOnNegative: Boolean,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val darkTheme: Boolean = false,
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = false
) : BaseDialogSetup {

    override fun create(): DialogFragment = DialogProgressFragment.create(this)

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

    constructor(
            id: Int,
            title: Text,
            text: Text?,
            horizontal: Boolean = true,
            negButton: Text? = null,
            dismissOnNegative: Boolean = false,
            darkTheme: Boolean = false) : this(id, title, text, horizontal, darkTheme = darkTheme, cancelable = false, negButton = negButton, dismissOnNegative = dismissOnNegative)
}