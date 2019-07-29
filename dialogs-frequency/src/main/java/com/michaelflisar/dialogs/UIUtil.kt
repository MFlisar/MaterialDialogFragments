package com.michaelflisar.dialogs

import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.classes.NoPaddingArrayAdapter
import com.michaelflisar.dialogs.classes.SendResultType
import com.michaelflisar.dialogs.classes.asText
import com.michaelflisar.dialogs.frequency.R
import com.michaelflisar.dialogs.setups.DialogDateTime
import com.michaelflisar.dialogs.setups.DialogNumber
import java.util.*

internal object UIUtil {
    fun setAdapter(
        onItemSelectedListener: AdapterView.OnItemSelectedListener,
        init: Boolean,
        spinner: Spinner,
        items: List<String>,
        selectedIndex: Int,
        alignTextRight: Boolean
    ) {
        if (init) {
            val adapter = NoPaddingArrayAdapter(
                spinner.context,
                android.R.layout.simple_spinner_dropdown_item,
                items,
                alignTextRight
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(selectedIndex, false)
            spinner.onItemSelectedListener = onItemSelectedListener
        } else {
            if (spinner.selectedItemPosition != selectedIndex) {
                spinner.setSelection(selectedIndex, false)
            }
        }
    }

    fun setEditText(
        fragment: BaseDialogFragment<*>,
        init: Boolean,
        editText: EditText,
        value: String,
        dateTime: Long?,
        dialogId: Int,
        title: String?,
        maxNumber: Int?
    ) {
        if (init) {
            editText.apply {
                isFocusable = false
                isFocusableInTouchMode = false
            }
        }
        editText.apply {
            setText(value)
            setOnClickListener {
                if (dateTime != null) {
                    DialogDateTime(
                        dialogId,
                        title = title?.asText(),
                        type = DialogDateTime.Type.DateOnly,
                        currentDateTime = Calendar.getInstance().apply { timeInMillis = dateTime }
                    )
                        .create()
                        .show(fragment, SendResultType.ParentFragment)
                } else {
                    val error = if (maxNumber == null) R.string.mdf_error_value_must_be_greater_than_zero.asText() else {
                        fragment.getString(R.string.mdf_error_value_must_be_between_1_and_X, maxNumber).asText()
                    }
                    DialogNumber(
                        dialogId,
                        title = title?.asText(),
                        min = 1,
                        max = maxNumber,
                        initialValue = value.toIntOrNull(),
                        errorMessage = error,
                        selectText = true
                    )
                        .create()
                        .show(fragment, SendResultType.ParentFragment)
                }
            }
        }
    }
}