package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.mikepenz.fastadapter.IItem

class DialogFastAdapterEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data?) :
    BaseDialogEvent(setup, buttonIndex) {

    class Data(private val selectedItem: IItem<*>, val index: Int) {
        @Suppress("UNCHECKED_CAST")
        fun <T> getItem(): T {
            return selectedItem as T
        }
    }
}