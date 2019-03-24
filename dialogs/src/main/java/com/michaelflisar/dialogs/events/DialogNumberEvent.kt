package com.michaelflisar.dialogs.events

import android.os.Bundle


class DialogNumberEvent(extras: Bundle?, id: Int, val values: List<Int>) : BaseDialogEvent(extras, id) {

    constructor(extras: Bundle?, id: Int, value: Int) : this(extras, id, arrayListOf(value))

    val value: Int = values[0]

    fun getValueAt(index: Int) = if (values.size > index) values[index] else null
}