package com.michaelflisar.dialogs

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.SendResultType
import com.michaelflisar.dialogs.classes.SimpleBaseDialogSetup
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.text.Text

// --------------
// MaterialDialog
// --------------

fun MaterialDialog.iconView(): ImageView? = findViewById(R.id.md_icon_title)

fun MaterialDialog.textView(): TextView? = findViewById(R.id.md_text_message)

fun MaterialDialog.titleView(): TextView? = findViewById(R.id.md_text_title)

fun MaterialDialog.dismissAnimated() {
    findViewById<ViewGroup>(R.id.md_root_bottom_sheet)?.let {
        val bottomSheetBehavior = BottomSheetBehavior.from(it)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        // behaviour does dismiss the sheet already automatically if sheet is hidden => this extension function depends on this fact and that it does not change
    } ?: dismiss()
}

fun MaterialDialog.message(text: Text?): MaterialDialog {
    when (text) {
        is Text.String -> message(text = text.text)
        is Text.Resource -> message(text.res)
        is Text.Empty,
        null -> {
        }
    }
    return this
}

fun MaterialDialog.title(setup: SimpleBaseDialogSetup): MaterialDialog {
    val text = setup.title
    when (text) {
        is Text.String -> title(text = text.text)
        is Text.Resource -> title(text.res)
        is Text.Empty,
        null -> {
        }
    }
    return this
}

fun MaterialDialog.positiveButton(
    setup: SimpleBaseDialogSetup,
    click: DialogCallback? = null
): MaterialDialog {
    val button = setup.posButton
    when (button) {
        is Text.String -> positiveButton(text = button.text, click = click)
        is Text.Resource -> positiveButton(res = button.res, click = click)
        is Text.Empty,
        null -> positiveButton(text = "", click = click)
    }
    return this
}

fun MaterialDialog.neutralButton(
    setup: SimpleBaseDialogSetup,
    click: DialogCallback? = null
): MaterialDialog {
    @Suppress("DEPRECATION")
    val button = setup.neutrButton
    when (button) {
        is Text.String -> neutralButton(text = button.text, click = click)
        is Text.Resource -> neutralButton(res = button.res, click = click)
        is Text.Empty,
        null -> neutralButton(text = "", click = click)
    }
    return this
}

fun MaterialDialog.negativeButton(
    setup: SimpleBaseDialogSetup,
    click: DialogCallback? = null
): MaterialDialog {
    val button = setup.negButton
    when (button) {
        is Text.String -> negativeButton(text = button.text, click = click)
        is Text.Resource -> negativeButton(res = button.res, click = click)
        is Text.Empty,
        null -> negativeButton(text = "", click = click)
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

internal fun Context.isCurrentThemeDark(): Boolean {
    val color = resolve(android.R.attr.colorBackground)
    val darkness =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness > 0.5
}

private fun Context.resolve(attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

// --------------
// Dialog Setups
// --------------

fun BaseDialogSetup.show(
    parent: FragmentActivity,
    customSendResultType: SendResultType? = DialogSetup.DEFAULT_SEND_RESULT_TYPE,
    tag: String = this::class.java.name,
    allowStateLoss: Boolean = false
) {
    create().apply { this.customSendResultType = customSendResultType }
        .show(parent, tag, allowStateLoss)
}

fun BaseDialogSetup.show(
    parent: Fragment,
    customSendResultType: SendResultType? = DialogSetup.DEFAULT_SEND_RESULT_TYPE,
    tag: String = this::class.java.name,
    allowStateLoss: Boolean = false
) {
    create().apply { this.customSendResultType = customSendResultType }
        .show(parent, tag, allowStateLoss)
}

fun <T : SimpleBaseDialogSetup> MaterialDialogFragment<T>.show(
    parent: FragmentActivity,
    tag: String,
    allowStateLoss: Boolean
) {
    val ft = parent.supportFragmentManager.beginTransaction().add(this, tag)
    if (allowStateLoss) ft.commitAllowingStateLoss() else ft.commit()
}

fun <T : SimpleBaseDialogSetup> MaterialDialogFragment<T>.show(
    parent: Fragment,
    tag: String,
    allowStateLoss: Boolean
) {
    val ft = parent.childFragmentManager.beginTransaction().add(this, tag)
    if (allowStateLoss) ft.commitAllowingStateLoss() else ft.commit()
}