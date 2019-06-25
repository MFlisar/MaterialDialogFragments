package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.enums.SendResultType

/**
 * Created by flisar on 23.09.2016.
 */

object DialogSetup {

    var DEFAULT_SEND_RESULT_TYPE = SendResultType.TargetOrParentOrFragmentOrActivity

    var resultHandler: ((event: Any) -> Unit)? = null

    var useDarkTheme: (() -> Boolean) = { false }

    var SEND_CANCEL_EVENT_BY_DEFAULT: Boolean = false

    fun sendResult(result: Any) {
        resultHandler?.invoke(result)
    }
}