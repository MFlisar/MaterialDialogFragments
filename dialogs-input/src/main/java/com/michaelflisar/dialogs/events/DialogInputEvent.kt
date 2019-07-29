package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup

class DialogInputEvent(setup: BaseDialogSetup, buttonIndex: Int, val data: Data?) : BaseDialogEvent(setup, buttonIndex) {

    class Data(val inputs: ArrayList<String>) {
        val input = inputs[0]
        fun getInput(index: Int = 0) = if (inputs.size > index) inputs[index] else null
    }
}