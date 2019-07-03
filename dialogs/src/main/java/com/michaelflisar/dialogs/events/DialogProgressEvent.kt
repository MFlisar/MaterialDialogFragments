package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup

class DialogProgressEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data?) : BaseDialogEvent(setup, buttonIndex) {
    class Data(closed: Boolean, closeForcedByNewDialog: Boolean)
}