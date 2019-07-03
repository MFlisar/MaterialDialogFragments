package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.FrequencySetup

class DialogFrequencyEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data?) :
    BaseDialogEvent(setup, buttonIndex) {

    class Data(val frequency: FrequencySetup)
}