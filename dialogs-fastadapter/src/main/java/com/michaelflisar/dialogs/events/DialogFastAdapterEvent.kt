package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.mikepenz.fastadapter.IItem

class DialogFastAdapterEvent(setup: BaseDialogSetup, private val selectedItem: IItem<*, *>?, val index: Int) : BaseDialogEvent(setup) {
//    private var item: IItem<*, *>? = null
//    var index: Int = 0

//    var neutral: Boolean = false
//
//    constructor(id: Int) : this(null, id, null, 0) {
//        neutral = true
//    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IItem<*, *>> getItem(): T? {
        return selectedItem as T?
    }
}