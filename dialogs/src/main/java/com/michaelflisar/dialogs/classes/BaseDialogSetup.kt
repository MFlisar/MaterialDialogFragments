package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.interfaces.DialogFragment

interface BaseDialogSetup : SimpleBaseDialogSetup {
    fun create(): DialogFragment<*>
}