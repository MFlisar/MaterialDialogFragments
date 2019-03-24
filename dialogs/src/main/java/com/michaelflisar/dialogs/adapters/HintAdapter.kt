package com.michaelflisar.dialogs.adapters

import android.content.Context
import android.widget.ArrayAdapter


open class HintAdapter<T>(context: Context, layoutId: Int, objects: List<T>) :
        ArrayAdapter<T>(context, layoutId, android.R.id.text1, objects) {
    private var hint = false

    fun enableHint(hintItem: T) {
        hint = true
        add(hintItem)
    }

    override fun getCount(): Int {
        if (hint) {
            // don't display last item. It is used as hint.
            val count = super.getCount()
            return if (count > 0) count - 1 else count
        } else {
            return super.getCount()
        }
    }
}