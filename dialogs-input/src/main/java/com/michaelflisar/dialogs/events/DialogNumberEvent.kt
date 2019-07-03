package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup


class DialogNumberEvent(setup: BaseDialogSetup, buttonIndex: Int, val data: Data?) : BaseDialogEvent(setup, buttonIndex) {

    class Data(val values: List<Int>) {

        constructor(value: Int) : this(arrayListOf(value))

        val value: Int = values[0]

        fun getValueAt(index: Int = 0) = if (values.size > index) values[index] else null
    }
}