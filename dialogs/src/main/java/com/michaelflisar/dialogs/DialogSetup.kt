package com.michaelflisar.dialogs

/**
 * Created by flisar on 23.09.2016.
 */

object DialogSetup {

    var resultHandler: ((event: Any) -> Unit)? = null

    fun sendResult(result: Any) {
        resultHandler?.invoke(result)
    }
}