package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.enums.MaterialDialogButton

/**
 * Created by flisar on 15.11.2016.
 */

interface MaterialDialogEvent {

    val extras: Bundle?
    val id: Int
    val button: MaterialDialogButton?

    fun hasExtra(key: String) = extras?.containsKey(key) ?: false

    @Suppress("UNCHECKED_CAST")
    fun <T> getExtra(key: String, defaultValue: T? = null): T? {
        if (hasExtra(key))
            return extras!!.get(key) as T
        return defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : MaterialDialogEvent> cast() = this as T
}
