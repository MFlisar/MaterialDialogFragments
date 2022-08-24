package com.michaelflisar.dialogs.enums

// ids must be the same as the index in MaterialDialogs
enum class MaterialDialogButton(val id: Int) {
    Positive(0),
    Negative(1),
    Neutral(2)
    ;

    companion object {
        fun find(index: Int) = values()[index]
    }
}