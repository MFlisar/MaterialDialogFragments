package com.michaelflisar.dialogs.classes

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.title
import com.michaelflisar.text.Text

interface SimpleBaseDialogSetup : Parcelable {

    val id: Int

    // Settings applied in createMaterialDialog
    val title: Text?
    val cancelable: Boolean
    val style: DialogStyle

    // Buttons
    val posButton: Text
    val negButton: Text?
    val neutrButton: Text?

    // others
    val extra: Bundle?
    val sendCancelEvent: Boolean

    /*
    * this functions applies the dialog style, title and cancelable flags
     */
    fun createMaterialDialog(activity: Activity, materialDialogFragment: MaterialDialogFragment<*>, applyTitle: Boolean = true): MaterialDialog {

        // 1) create dialog + apply bottom sheet style
        val s = style
        val dialog = when (s) {
            is DialogStyle.Dialog -> MaterialDialog(activity)
            is DialogStyle.BottomSheet -> {
                // no animation, because the fragment will call the the dialogs show function after screen off/on and material-dialogs will animate in this event!
                val bottomSheet = BottomSheet(if (s.layoutModeMatchParent) LayoutMode.MATCH_PARENT else LayoutMode.WRAP_CONTENT, s.initialState, true)
                val dlg = MaterialDialog(activity, bottomSheet)
                if (s.peekHeight != null || s.resPeekHeight != null) {
                    dlg.setPeekHeight(s.peekHeight, s.resPeekHeight)
                }
                // BUGFIX: not 100% working but fixes the animation problem that comes for the single input view by the opening keyboard overlaying the bottom sheet
                dlg.view.postDelayed({
                    dlg.findViewById<ViewGroup>(R.id.md_root_bottom_sheet)?.let {
                        it.post {
                            val bottomSheetBehavior = BottomSheetBehavior.from(it)
                            bottomSheetBehavior.state = s.initialState
                        }
                    }
                }, 250L)//BottomSheet.LAYOUT_PEEK_CHANGE_DURATION_MS)

                dlg
            }
        }

        // 2) apply title
        if (applyTitle) {
            dialog.title(this)
        }

        // 3) apply cancelable
        dialog.cancelable(cancelable)
        dialog.cancelOnTouchOutside(cancelable)
        materialDialogFragment.isCancelable = cancelable

        return dialog
    }
}