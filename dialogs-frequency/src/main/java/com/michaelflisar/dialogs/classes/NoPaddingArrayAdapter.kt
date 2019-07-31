package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

internal class NoPaddingArrayAdapter<T>(
        context: Context,
        layoutId: Int,
        items: List<T>?,
        private val alignTextRight: Boolean = false
) : ArrayAdapter<T>(context, layoutId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = super.getView(position, convertView, parent)
        view.setPadding(0, view.paddingTop, view.paddingRight, view.paddingBottom)
        if (alignTextRight) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                (view as? TextView)?.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            } else {
                (view as? TextView)?.gravity = Gravity.END
            }
        }
        return view
    }
}