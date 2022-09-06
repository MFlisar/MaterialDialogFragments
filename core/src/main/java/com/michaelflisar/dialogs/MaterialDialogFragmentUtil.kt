package com.michaelflisar.dialogs

import android.R
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment


object MaterialDialogFragmentUtil {

    private const val ARG_SETUP = "setup"
    private const val ARG_DIALOG = "dialog"

    fun <S : MaterialDialogSetup<S, *>> createArguments(
        fragment: Fragment,
        setup: S,
        dialog: Boolean
    ): Bundle {
        val args = fragment.arguments ?: Bundle()
        args.putParcelable(ARG_SETUP, setup)
        args.putBoolean(ARG_DIALOG, dialog)
        return args
    }

    fun <S : MaterialDialogSetup<S, *>> getSetup(fragment: Fragment) =
        fragment.requireArguments().getParcelable<S>(ARG_SETUP)!!

    fun getShowAsDialog(fragment: Fragment) =
        fragment.requireArguments().getBoolean(ARG_DIALOG)!!

    fun getThemeColorAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun getThemeReference(context: Context, attribute: Int): Int {
        val typeValue = TypedValue()
        context.theme.resolveAttribute(attribute, typeValue, false)
        return if (typeValue.type == TypedValue.TYPE_REFERENCE) {
            typeValue.data
        } else {
            -1
        }
    }

    fun pxToDp(px: Int ): Int = (px / Resources.getSystem().displayMetrics.density).toInt()
    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun isCurrentThemeDark(context: Context): Boolean {
        val color = resolve(context, android.R.attr.colorBackground)
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness > 0.5
    }

    fun resolve(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    fun isDark(context: Context): Boolean {
        return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    internal fun createContentView(fragment: MaterialDialogFragment<*, *, *>, content: View): View {
        if (fragment.wrapInScrollContainer) {
            val scrollView = NestedScrollView(fragment.requireContext())
            scrollView.addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            return scrollView
        } else return content
    }
}