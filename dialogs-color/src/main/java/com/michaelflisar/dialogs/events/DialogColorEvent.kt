package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.setups.DialogColor

class DialogColorEvent(setup: DialogColor, buttonIndex: Int?, val data: Data?) : BaseDialogEvent(setup, buttonIndex) {
    class Data(var color: Int)
}