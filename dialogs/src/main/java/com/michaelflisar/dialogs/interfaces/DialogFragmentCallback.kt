package com.michaelflisar.dialogs.interfaces

import com.michaelflisar.dialogs.events.BaseDialogEvent

/**
 * Created by flisar on 21.02.2017.
 */

interface DialogFragmentCallback {
    fun onDialogResultAvailable(event: BaseDialogEvent)
}
