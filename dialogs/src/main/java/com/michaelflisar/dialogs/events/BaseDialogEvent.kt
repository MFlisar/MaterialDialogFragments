package com.michaelflisar.dialogs.events

import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Constants

/**
 * Created by flisar on 15.11.2016.
 */

open class BaseDialogEvent(val extras: Bundle?, val id: Int, private val buttonIndex: Int?) {

    constructor(setup: BaseDialogSetup, buttonIndex: Int?) : this(setup.extra, setup.id, buttonIndex)

    fun hasExtra(key: String) = extras?.containsKey(key) ?: false

    @Suppress("UNCHECKED_CAST")
    fun <T> getExtra(key: String, defaultValue: T? = null): T? {
        if (hasExtra(key))
            return extras!!.get(key) as T
        return defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseDialogEvent> cast() = this as T

    fun posClicked() =  buttonIndex == Constants.INDEX_POSITIVE
    fun neutrClicked() =  buttonIndex == Constants.INDEX_NEUTRAL
    fun negClicked() =  buttonIndex == Constants.INDEX_NEGATIVE
}
