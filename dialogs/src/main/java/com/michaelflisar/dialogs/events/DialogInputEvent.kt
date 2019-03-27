package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup

sealed class DialogInputEvent(setup: BaseDialogSetup) : BaseDialogEvent(setup) {

    class Input(setup: BaseDialogSetup, val inputs: ArrayList<String>) : DialogInputEvent(setup) {
        fun getInput(index: Int = 0) = if (inputs.size > index) inputs[index] else null
    }

    class NeutralButton(setup: BaseDialogSetup) : DialogInputEvent(setup)

}