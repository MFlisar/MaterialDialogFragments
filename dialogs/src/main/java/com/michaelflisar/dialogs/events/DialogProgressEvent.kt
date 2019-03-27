package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup

sealed class DialogProgressEvent(setup: BaseDialogSetup) : BaseDialogEvent(setup) {
    class Closed(setup: BaseDialogSetup) : DialogProgressEvent(setup)
    class Cancelled(setup: BaseDialogSetup, val forcedByNewDialog: Boolean) : DialogProgressEvent(setup)
}