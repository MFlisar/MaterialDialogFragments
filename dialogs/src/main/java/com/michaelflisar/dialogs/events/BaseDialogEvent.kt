package com.michaelflisar.dialogs.events

import android.os.Bundle

/**
 * Created by flisar on 15.11.2016.
 */

open class BaseDialogEvent(val extras: Bundle?, val id: Int) {

    fun hasExtra(key: String) = extras?.containsKey(key) ?: false

    @Suppress("UNCHECKED_CAST")
    fun <T> getExtra(key: String, defaultValue: T? = null): T? {
        if (hasExtra(key))
            return extras!!.get(key) as T
        return defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseDialogEvent> cast() = this as T
}
