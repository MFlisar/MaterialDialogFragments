package com.michaelflisar.dialogs

import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.MaterialDialogKey
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.dialogs.internal.classes.MaterialDialogEventListenerWrapper

object MaterialDialog {

    private val listeners: ArrayList<MaterialDialogEventListenerWrapper<*>> = ArrayList()
    private val activeListeners: ArrayList<MaterialDialogEventListenerWrapper<*>> = ArrayList()

    // --------------
    // public functions
    // --------------

    var defaults = MaterialDefaultSettings()

    inline fun <reified E : MaterialDialogEvent> onEvent(
        lifecycleOwner: LifecycleOwner,
        id: Int? = null,
        noinline listener: (event: E) -> Unit
    ) {
        // create the wrapper - it takes care of everything!
        val key = if (id == null) MaterialDialogKey.Simple(
            E::class.java
        ) else MaterialDialogKey.ID(
            E::class.java,
            id
        )
        val wrapper = createWrapper(lifecycleOwner, key, listener)
    }

    fun <E : MaterialDialogEvent> sendEvent(event: E) {
        // Filter: Listener must be for same dialog setup and for the event class (or any sub class)
        activeListeners
            // check 1: event class must be of type E or any sub class
            .filter { it.key.classEvent.isAssignableFrom(event::class.java) }
            // check 2: eventually check the event id
            .filter {
                when (it.key) {
                    is MaterialDialogKey.ID -> it.key.id == event.id
                    is MaterialDialogKey.Simple -> true// event class was already checked
                }
            }.forEach {
                // cast is safe because of check 2
                (it.listener as (event: E) -> Unit).invoke(event)
            }
    }

    // --------------
    // Listener
    // --------------

    internal fun addListener(listener: MaterialDialogEventListenerWrapper<*>) {
        listeners.add(listener)
    }

    internal fun removeListener(listener: MaterialDialogEventListenerWrapper<*>) {
        listeners.remove(listener)
        activeListeners.remove(listener)
    }

    internal fun setState(listener: MaterialDialogEventListenerWrapper<*>, active: Boolean) {
        if (active)
            activeListeners.add(listener)
        else
            activeListeners.remove(listener)
    }

    // --------------
    // helper function to hide MaterialDialogEventListenerWrapper as internal class
    // --------------

    fun <E : MaterialDialogEvent> createWrapper(
        lifecycleOwner: LifecycleOwner,
        key: MaterialDialogKey,
        listener: (event: E) -> Unit
    ) {
        val wrapper = MaterialDialogEventListenerWrapper(lifecycleOwner, key, listener)
    }
}