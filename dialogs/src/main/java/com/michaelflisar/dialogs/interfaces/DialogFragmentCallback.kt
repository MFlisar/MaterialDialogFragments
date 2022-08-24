package com.michaelflisar.dialogs.interfaces

import com.michaelflisar.dialogs.events.MaterialDialogEvent

/**
 * Created by flisar on 21.02.2017.
 */

interface DialogFragmentCallback {
    fun onDialogResultAvailable(event: MaterialDialogEvent): Boolean
}
