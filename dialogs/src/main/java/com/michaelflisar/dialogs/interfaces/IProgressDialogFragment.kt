package com.michaelflisar.dialogs.interfaces

import com.michaelflisar.dialogs.classes.Text

interface IProgressDialogFragment {
    fun update(event: Text)
    fun close(forcedByNewDialog: Boolean)
}