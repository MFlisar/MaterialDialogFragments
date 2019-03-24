package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Text : Parcelable {

    @Parcelize
    class TextString(val text: String) : Text()

    @Parcelize
    class TextRes(val res: Int) : Text()

    fun get(context: Context): String {
        return when (this) {
            is TextString -> text
            is TextRes -> context.getString(res)
        }
    }
}

fun Int.asText() : Text {
    return Text.TextRes(this)
}

fun String.asText() : Text {
    return Text.TextString(this)
}