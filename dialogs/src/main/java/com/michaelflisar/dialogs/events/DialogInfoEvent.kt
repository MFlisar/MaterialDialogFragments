package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Constants

class DialogInfoEvent(setup: BaseDialogSetup, var buttonIndex: Int) : BaseDialogEvent(setup) {
    fun posClicked() =  buttonIndex == Constants.INDEX_POSITIVE
    fun neutrClicked() =  buttonIndex == Constants.INDEX_NEUTRAL
    fun negClicked() =  buttonIndex == Constants.INDEX_NEGATIVE
}