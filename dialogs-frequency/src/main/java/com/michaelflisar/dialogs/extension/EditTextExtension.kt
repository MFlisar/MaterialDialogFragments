package com.michaelflisar.dialogs.extension

import android.widget.EditText
import com.michaelflisar.dialogs.classes.SimpleTextWatcher

internal fun EditText.setSimpleTextWatcher(onTextChanged: ((text: String) -> Unit)) {
    addTextChangedListener(SimpleTextWatcher(onTextChanged))
}