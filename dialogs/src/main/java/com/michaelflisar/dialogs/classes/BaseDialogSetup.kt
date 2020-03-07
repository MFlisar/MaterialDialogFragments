package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.base.MaterialDialogFragment

interface BaseDialogSetup : SimpleBaseDialogSetup {
    fun create(): MaterialDialogFragment<*>
}