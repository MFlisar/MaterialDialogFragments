package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.enums.SendResultType

/**
 * Created by flisar on 23.09.2016.
 */

object DialogSetup {

    var DEFAULT_SEND_RESULT_TYPE = SendResultType.TargetOrParentOrActivity

    var resultHandler: ((event: Any) -> Unit)? = null

    fun sendResult(result: Any) {
        resultHandler?.invoke(result)
    }
}