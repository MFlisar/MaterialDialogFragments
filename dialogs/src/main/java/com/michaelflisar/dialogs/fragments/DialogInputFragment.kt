package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.events.DialogInputEvent
import com.michaelflisar.dialogs.setups.DialogInput
import com.michaelflisar.dialogs.utils.KeyboardUtils

class DialogInputFragment : BaseDialogFragment() {

    companion object {

        fun create(setup: DialogInput): DialogInputFragment {
            val dlg = DialogInputFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var setup: DialogInput

    private var inputTexts: ArrayList<String> = arrayListOf()

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = getSetup()

        val inputFields = arrayListOf(setup.input)
        inputFields.addAll(setup.additonalInputs)
        // TODO: add views like in the dialog number picker to allow any number of inputs
        if (inputFields.size > 2) {
            throw RuntimeException("Currently only 1 or 2 inputs are supported!")
        }

        if (savedInstanceState != null) {
            inputTexts = savedInstanceState.getStringArrayList("inputTexts")!!
        } else {
            inputTexts.addAll(inputFields.map { it.initialText?.get(activity!!) ?: "" })
        }

        val dialog: MaterialDialog
        if (inputTexts.size == 1) {
            dialog = MaterialDialog(activity!!)

            inputFields[0].label?.let {
                dialog.message(it)
            }

            dialog
                    .input(
                            waitForPositiveButton = false,
                            allowEmpty = setup.allowEmptyText,
                            hint = inputFields[0].hint?.get(activity!!) ?: "",
                            prefill = inputTexts[0],
                            inputType = setup.inputType
                    ) { materialDialog: MaterialDialog, charSequence: CharSequence ->
                        inputTexts[0] = charSequence.toString()
                        val valid = setup.allowEmptyText || inputTexts[0].isNotEmpty()
                        materialDialog.setActionButtonEnabled(WhichButton.POSITIVE, valid)
                    }
            dialog.positiveButton(setup.posButton) {
                finishAndSendEvent(it)
            }
        } else {
            dialog = MaterialDialog(activity!!)
                    .customView(R.layout.dialog_multi_input, scrollable = true)
            dialog.positiveButton(setup.posButton) {
                val customView = it.getCustomView()
                val editText1: EditText = customView.findViewById(R.id.etText1)
                val editText2: EditText = customView.findViewById(R.id.etText2)
                inputTexts[0] = editText1.text.toString()
                inputTexts[1] = editText2.text.toString()
                finishAndSendEvent(it)
            }
        }

        dialog
                .cancelable(setup.cancelable)
                .noAutoDismiss()

        isCancelable = setup.cancelable

        dialog.title(setup.title)

        setup.neutrButton?.let {
            dialog
                    .neutralButton(it) {
                        when (setup.neutralButtonMode) {
                            DialogInput.NeutralButtonMode.SendEvent -> {
                                sendEvent(DialogInputEvent.NeutralButton(setup))
                                dismiss()
                            }
                            DialogInput.NeutralButtonMode.InsertText -> {
                                setup.textToInsertOnNeutralButtonClick?.get(activity!!)?.let {
                                    dialog.getInputField().append(it)
                                }
                            }
                        }
                    }
        }

        val editTexts = ArrayList<EditText>()
        val textViews = ArrayList<TextView?>()

        if (inputTexts.size == 1) {
            val editText = dialog.getInputField()
            editTexts.add(editText)
            textViews.add(dialog.textView())

            setup.textSize?.let {
                dialog.textView()!!.setTextSize(it)
            }
            if (setup.selectText) {
                editText.post {
                    editText.setSelection(0, editText.text.toString().length)
                    editText.selectAll()
                    //editText.setSelectAllOnFocus(true);//.setSelectAllOnFocus(true);
                }
            }
            textViews.add(dialog.textView())
        } else {
            val customView = dialog.getCustomView()
            val editText1: EditText = customView.findViewById(R.id.etText1)
            val editText2: EditText = customView.findViewById(R.id.etText2)
            editTexts.add(editText1)
            editTexts.add(editText2)

            val tvText1: TextView = customView.findViewById(R.id.tvText1)
            val tvText2: TextView = customView.findViewById(R.id.tvText2)
            textViews.add(tvText1)
            textViews.add(tvText2)

            if (!setup.allowEmptyText) {
                fun shouldPositiveButtonBeEnabled() = inputTexts[0].length > 0 && inputTexts[1].length > 0
                dialog.getActionButton(WhichButton.POSITIVE).isEnabled = shouldPositiveButtonBeEnabled()
                for (i in 0 until editTexts.size) {
                    editTexts[i].addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            inputTexts[i] = s.toString()
                            dialog.getActionButton(WhichButton.POSITIVE).isEnabled = shouldPositiveButtonBeEnabled()
                        }
                    })
                }
            }

            for (i in 0 until Math.max(inputTexts.size, 2)) {
                if (i < inputTexts.size) {
                    val inputText = inputTexts[i]
                    val label = inputFields[i].label?.get(activity!!)
                    val hint = inputFields[i].hint?.get(activity!!)
                    if (label?.length ?: 0 > 0) {
                        textViews[i]!!.text = label
                        textViews[i]!!.visibility = View.VISIBLE
                        setup.textSize?.let {
                            textViews[i]!!.textSize = it
                        }
                    } else {
                        textViews[i]?.visibility = View.GONE
                    }
                    editTexts[i].setText(inputText)
                    editTexts[i].minLines = setup.minLines
                    setup.inputTextSize?.let {
                        editTexts[i].textSize = it
                    }
                    hint?.let {
                        editTexts[i].hint = it
                    }

                } else {
                    textViews[i]?.visibility = View.GONE
                    editTexts[i].visibility = View.GONE
                }
            }
        }

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("inputTexts", inputTexts)
    }

    private fun finishAndSendEvent(materialDialog: MaterialDialog) {
        sendEvent(DialogInputEvent.Input(setup, inputTexts))
        if (activity != null) {
            KeyboardUtils.hideKeyboardWithZeroFlag(activity, materialDialog.currentFocus)
        }
        dismiss()
    }
}