package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.text.Text

data class MaterialDefaultSettings(
    val style: DialogStyle = DialogStyle.Dialog,
    val buttonPositive: Text = Text.Resource(android.R.string.ok),
    val buttonNegative: Text = Text.Empty,
    val buttonNeutral: Text = Text.Empty,
    val cancelable: Boolean = true,
    val swipeDismissable: Boolean = true
)