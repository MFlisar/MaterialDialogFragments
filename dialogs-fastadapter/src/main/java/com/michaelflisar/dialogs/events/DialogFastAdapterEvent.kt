package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.mikepenz.fastadapter.IItem

class DialogFastAdapterEvent(extra: Bundle?, id: Int, private val selectedItem: IItem<*, *>?, val index: Int) : BaseDialogEvent(extra, id) {
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