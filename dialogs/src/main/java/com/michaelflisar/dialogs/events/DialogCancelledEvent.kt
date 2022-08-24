package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.MaterialDialogEventImpl
import com.michaelflisar.dialogs.classes.SimpleBaseDialogSetup

class DialogCancelledEvent(setup: SimpleBaseDialogSetup) : MaterialDialogEvent by MaterialDialogEventImpl(setup, null)