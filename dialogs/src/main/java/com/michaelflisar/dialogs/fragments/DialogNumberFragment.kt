package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.events.DialogNumberEvent
import com.michaelflisar.dialogs.message
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogNumber
import com.michaelflisar.dialogs.title
import com.michaelflisar.dialogs.utils.KeyboardUtils

class DialogNumberFragment : BaseDialogFragment() {

    companion object {

        fun create(setup: DialogNumber): DialogNumberFragment {
            val dlg = DialogNumberFragment()
            val args = Bundle().apply {
                putParcelable("setup", setup)
            }
            dlg.arguments = args
            return dlg
        }
    }

    private lateinit var setup: DialogNumber
    private var input: Int? = null

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = arguments!!.getParcelable("setup")!!

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("input"))
                input = savedInstanceState.getInt("input")
        } else
            input = setup.initialValue

        val dialog = MaterialDialog(activity!!)
                .title(setup.title)
                .positiveButton(setup.posButton) {
                    input = it.getInputField().text.toString().toInt()
                    if ((setup.min != null && input!! < setup.min!!) || (setup.max != null && input!! > setup.max!!)) {
                        setup.errorMessage?.get(activity!!)?.let {
                            // TODO: instead of a toast this should be part of the layout => needs a custom layout
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        sendEvent(DialogNumberEvent(extra, setup.id, input!!))
                        val activity = activity
                        KeyboardUtils.hideKeyboard(activity, it.currentFocus)
                        dismiss()
                    }
                }
                .cancelable(true)
                .noAutoDismiss()

        setup.text?.let {
            dialog.message(it)
        }

        dialog.input(
                waitForPositiveButton = false,
                hint = setup.hint?.get(activity!!) ?: "",
                prefill = input?.toString() ?: "",
                inputType = InputType.TYPE_CLASS_NUMBER) { materialDialog: MaterialDialog, charSequence: CharSequence ->
            val valid = charSequence.toString().length > 0
            if (valid) {
                input = charSequence.toString().toInt()
            } else {
                input = null
            }
            materialDialog.setActionButtonEnabled(WhichButton.POSITIVE, valid)
        }


        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        input?.let {
            outState.putInt("input", it)
        }

    }
}
