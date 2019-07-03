package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import java.util.*

class DialogDateTimeEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data? = null) :
    BaseDialogEvent(setup, buttonIndex) {

    class Data(val date: Calendar)
}