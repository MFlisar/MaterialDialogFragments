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
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.setups.DialogNumber
import com.michaelflisar.dialogs.utils.KeyboardUtils

class DialogNumberFragment : BaseDialogFragment<DialogNumber>() {

    companion object {

        fun create(setup: DialogNumber): DialogNumberFragment {
            val dlg = DialogNumberFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private var input: Int? = null

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("input"))
                input = savedInstanceState.getInt("input")
        } else
            input = setup.initialValue

        val dialog = MaterialDialog(activity!!)
                .positiveButton(setup.posButton) {
                    input = it.getInputField().text.toString().toInt()
                    if ((setup.min != null && input!! < setup.min!!) || (setup.max != null && input!! > setup.max!!)) {
                        setup.errorMessage?.get(activity!!)?.let {
                            // TODO: instead of a toast this should be part of the layout => needs a custom layout
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        sendEvent(
                            com.michaelflisar.dialogs.events.DialogNumberEvent(
                                setup,
                                WhichButton.POSITIVE.ordinal,
                                com.michaelflisar.dialogs.events.DialogNumberEvent.Data(input!!)
                            )
                        )
                        val activity = activity
                        KeyboardUtils.hideKeyboard(activity, it.currentFocus)
                        dismiss()
                    }
                }
                .cancelable(true)
                .noAutoDismiss()

        setup.title?.let {
            dialog.title(it)
        }

        setup.negButton?.let {
            dialog.negativeButton(it) {
                sendEvent(com.michaelflisar.dialogs.events.DialogNumberEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                dismiss()
            }
        }

        setup.neutrButton?.let {
            dialog.neutralButton(it) {
                sendEvent(com.michaelflisar.dialogs.events.DialogNumberEvent(setup, WhichButton.NEUTRAL.ordinal, null))
            }
        }

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

        val editText = dialog.getInputField()
        if (setup.selectText) {
            editText.post {
                editText.setSelection(0, editText.text.toString().length)
                editText.selectAll()
            }
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
