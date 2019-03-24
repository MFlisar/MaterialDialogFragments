package com.michaelflisar.dialogs.events

sealed class DialogProgressEvent(id: Int) : BaseDialogEvent(null, id) {
    class Closed(id: Int) : DialogProgressEvent(id)
    class Cancelled(id: Int, val forcedByNewDialog: Boolean) : DialogProgressEvent(id)
}