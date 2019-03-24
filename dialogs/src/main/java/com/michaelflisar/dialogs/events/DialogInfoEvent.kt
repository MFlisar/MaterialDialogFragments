package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.Constants

class DialogInfoEvent(extra: Bundle?, id: Int, var buttonIndex: Int) : BaseDialogEvent(extra, id) {
    fun posClicked() =  buttonIndex == Constants.INDEX_POSITIVE
    fun neutrClicked() =  buttonIndex == Constants.INDEX_NEUTRAL
    fun negClicked() =  buttonIndex == Constants.INDEX_NEGATIVE
}