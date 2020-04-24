package com.michaelflisar.dialogs.interfaces

import com.michaelflisar.text.Text

interface IProgressDialogFragment {
    fun update(event: Text)
    fun close(forcedByNewDialog: Boolean)
}