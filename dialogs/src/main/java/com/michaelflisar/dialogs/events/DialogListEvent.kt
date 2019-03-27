package com.michaelflisar.dialogs.events

import android.os.Bundle

class DialogListEvent(extra: Bundle?, id: Int, val indizes: List<Int>, val items: List<Any>) : BaseDialogEvent(extra, id) {

    constructor(extra: Bundle?, id: Int, index: Int, item: Any) : this(extra, id, arrayListOf(index), arrayListOf(item))

    val itemCount = indizes.size

    fun getIndex(i: Int = 0) = indizes[i]

    fun getItem(i: Int = 0) = items[i]
}