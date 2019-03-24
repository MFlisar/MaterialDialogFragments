package com.michaelflisar.dialogs.utils

import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback

/**
 * Created by Michael on 14.05.2017.
 */

object DialogUtil {
    fun trySendResult(event: BaseDialogEvent, receiver: Any?) {
        if (receiver != null && receiver is DialogFragmentCallback) {
            receiver.onDialogResultAvailable(event)
        }
    }
}
