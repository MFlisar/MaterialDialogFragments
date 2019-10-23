package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.SimpleBaseDialogSetup

class DialogCancelledEvent(setup: SimpleBaseDialogSetup) : BaseDialogEvent(setup, null)