package com.michaelflisar.dialogs.internal.classes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.dialogs.classes.MaterialDialogKey
import com.michaelflisar.dialogs.MaterialDialogSetup

internal class MaterialDialogEventListenerWrapper<E: MaterialDialogEvent>(
    lifecycleOwner: LifecycleOwner,
    val key: MaterialDialogKey,
    val listener: (event: E) -> Unit
) : DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        MaterialDialog.addListener(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        MaterialDialog.setState(this, true)
    }

    override fun onStop(owner: LifecycleOwner) {
        MaterialDialog.setState(this, false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        MaterialDialog.removeListener(this)
    }
}