package com.michaelflisar.dialogs.classes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

internal class NoPaddingArrayAdapter<T>(
    context: Context,
    layoutId: Int,
    items: List<T>?,
    val alignTextRight: Boolean = true
) :
    ArrayAdapter<T>(context, layoutId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = super.getView(position, convertView, parent)
        view.setPadding(0, view.paddingTop, view.paddingRight, view.paddingBottom)
        if (alignTextRight) {
            (view as? TextView)?.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        }
        return view
    }
}