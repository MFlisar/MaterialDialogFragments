package com.michaelflisar.dialogs.events

import android.os.Bundle

sealed class DialogInputEvent(extra: Bundle?, id: Int) : BaseDialogEvent(extra, id) {

    class Input(extra: Bundle?, id: Int, val inputs: ArrayList<String>) : DialogInputEvent(extra, id) {
        fun getInput(index: Int = 0) = if (inputs.size > index) inputs[index] else null
    }

    class NeutralButton(extra: Bundle?, id: Int) : DialogInputEvent(extra, id)

}