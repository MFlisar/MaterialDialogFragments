package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.mikepenz.fastadapter.IItem

class DialogFastAdapterEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data<*>?) :
        BaseDialogEvent(setup, buttonIndex) {

    @Suppress("UNCHECKED_CAST")
    class Data<Item : IItem<*>>(val indizes: List<Int>, val items: List<Item>) {
        constructor(index: Int, item: Item) : this(arrayListOf(index), arrayListOf(item))
    }
}