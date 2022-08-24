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
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.message
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogNumber
import com.michaelflisar.dialogs.utils.KeyboardUtils

class DialogNumberFragment : MaterialDialogFragment<DialogNumber>() {

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

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(requireActivity(), this)

        dialog
            .positiveButton(setup) {
                input = it.getInputField().text.toString().toInt()
                if ((setup.min != null && input!! < setup.min!!) || (setup.max != null && input!! > setup.max!!)) {
                    setup.errorMessage?.get(requireActivity())?.let {
                        // TODO: instead of a toast this should be part of the layout => needs a custom layout
                        Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    sendEvent(
                        DialogNumber.Event.Data(
                            setup,
                            MaterialDialogButton.Positive,
                            input!!
                        )
                    )
                    val activity = activity
                    KeyboardUtils.hideKeyboard(activity, it.currentFocus)
                    dismiss()
                }
            }
            .negativeButton(setup) {
                sendEvent(DialogNumber.Event.Empty(setup, MaterialDialogButton.Negative))
                dismiss()
            }
            .neutralButton(setup) {
                sendEvent(DialogNumber.Event.Empty(setup, MaterialDialogButton.Neutral))
            }
            .message(setup.text)
        dialog
            .input(
                waitForPositiveButton = false,
                hint = setup.hint?.get(requireActivity()) ?: "",
                prefill = input?.toString() ?: "",
                inputType = InputType.TYPE_CLASS_NUMBER
            ) { materialDialog: MaterialDialog, charSequence: CharSequence ->
                input = charSequence.toString().toIntOrNull()
                materialDialog.setActionButtonEnabled(WhichButton.POSITIVE, input != null)
            }
        dialog.noAutoDismiss()

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
