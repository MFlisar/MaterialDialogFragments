package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup

class DialogListEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data?) : BaseDialogEvent(setup, buttonIndex) {

    @Suppress("UNCHECKED_CAST")
    class Data(val indizes: List<Int>, val items: List<Any>) {
        constructor(index: Int, item: Any) : this(arrayListOf(index), arrayListOf(item))

        val itemCount = indizes.size

        val index = indizes.firstOrNull() ?: -1
        fun <T> getItem()  = items.firstOrNull() as T?

        fun getIndex(i: Int = 0) = indizes[i]
        fun <T> getItem(i: Int = 0) = items[i] as T
    }
}