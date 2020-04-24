package com.michaelflisar.dialogs

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.michaelflisar.dialogs.classes.SimpleBaseDialogSetup
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.text.Text

// --------------
// MaterialDialog
// --------------

// old name: textViewMessage
//fun MaterialDialog.textView(): TextView? = javaClass.getDeclaredField("messageTextView").let {
//    it.isAccessible = true
//    return it.get(this) as TextView?
//}

fun MaterialDialog.iconView(): ImageView? = findViewById(R.id.md_icon_title)

fun MaterialDialog.textView(): TextView? = findViewById(R.id.md_text_message)

fun MaterialDialog.titleView(): TextView? = findViewById(R.id.md_text_title)

fun MaterialDialog.message(text: Text?): MaterialDialog {
    when (text) {
        is Text.String -> message(text = text.text)
        is Text.Resource -> message(text.res)
    }
    return this
}

fun MaterialDialog.title(setup: SimpleBaseDialogSetup): MaterialDialog {
    val text = setup.title
    when (text) {
        is Text.String -> title(text = text.text)
        is Text.Resource -> title(text.res)
    }
    return this
}

fun MaterialDialog.positiveButton(setup: SimpleBaseDialogSetup, click: DialogCallback? = null): MaterialDialog {
    val button = setup.posButton
    when (button) {
        is Text.String -> positiveButton(text = button.text, click = click)
        is Text.Resource -> positiveButton(res = button.res, click = click)
    }
    return this
}

fun MaterialDialog.neutralButton(setup: SimpleBaseDialogSetup, click: DialogCallback? = null): MaterialDialog {
    @Suppress("DEPRECATION")
    val button = setup.neutrButton
    when (button) {
        is Text.String -> neutralButton(text = button.text, click = click)
        is Text.Resource -> neutralButton(res = button.res, click = click)
    }
    return this
}

fun MaterialDialog.negativeButton(setup: SimpleBaseDialogSetup, click: DialogCallback? = null): MaterialDialog {
    val button = setup.negButton
    when (button) {
        is Text.String -> negativeButton(text = button.text, click = click)
        is Text.Resource -> negativeButton(res = button.res, click = click)
    }
    return this
}

// --------------
// Context
// --------------

internal fun Context.getThemeReference(attribute: Int): Int {
    val typeValue = TypedValue()
    theme.resolveAttribute(attribute, typeValue, false)
    return if (typeValue.type == TypedValue.TYPE_REFERENCE) {
        typeValue.data
    } else {
        -1
    }
}

internal fun Context.dpToPx(dp: Float): Int {
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    return px.toInt()
}