package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup

class DialogListEvent(setup: BaseDialogSetup, val indizes: List<Int>, val items: List<Any>) : BaseDialogEvent(setup) {

    constructor(setup: BaseDialogSetup, index: Int, item: Any) : this(setup, arrayListOf(index), arrayListOf(item))

    val itemCount = indizes.size

    fun getIndex(i: Int = 0) = indizes[i]

    fun <T> getItem(i: Int = 0) = items[i] as T
}