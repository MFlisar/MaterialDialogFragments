package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.DialogLogger
import com.michaelflisar.dialogs.classes.SendResultType

/**
 * Created by flisar on 23.09.2016.
 */

object DialogSetup {

    /**
     * define how the result of a dialog is send and where it is send to
     */
    var DEFAULT_SEND_RESULT_TYPE: SendResultType = SendResultType.First()

    /**
     * provide a custom dialog result handler - it will get ALL results
     */
    var resultHandler: ((event: Any) -> Unit)? = null

    /**
     * define if your app currently uses a dark or a light theme
     */
    var useDarkTheme: (() -> Boolean) = { false }

    /**
     * enable this, if you also want to be informed about cancel events (cancelled by touch outside and by back press)
     */
    var SEND_CANCEL_EVENT_BY_DEFAULT: Boolean = false

    /**
     * provide a custom logger
     */
    var logger: DialogLogger? = null

    internal fun sendResult(result: Any) {
        resultHandler?.invoke(result)
    }
}