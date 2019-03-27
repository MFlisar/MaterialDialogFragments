package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup


class DialogNumberEvent(setup: BaseDialogSetup, val values: List<Int>) : BaseDialogEvent(setup) {

    constructor(setup: BaseDialogSetup, value: Int) : this(setup, arrayListOf(value))

    val value: Int = values[0]

    fun getValueAt(index: Int = 0) = if (values.size > index) values[index] else null
}