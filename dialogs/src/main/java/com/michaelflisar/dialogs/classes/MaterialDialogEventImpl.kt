package com.michaelflisar.dialogs.classes

import android.os.Bundle
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.events.MaterialDialogEvent

class MaterialDialogEventImpl(
    override val extras: Bundle?,
    override val id: Int,
    override val button: MaterialDialogButton?
) : MaterialDialogEvent {

    constructor(setup: SimpleBaseDialogSetup, button: MaterialDialogButton?) : this(setup.extra, setup.id, button)
}