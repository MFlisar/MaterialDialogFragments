package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.utils.DialogUtil

abstract class BaseBottomDialogFragment : DialogFragment(), BaseDialogFragmentHandler.IBaseBottomDialog {

    // -----------------------------
    // forward functions to handler
    // -----------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dlg = handler.onCreateDialog(savedInstanceState)
        onDialogCreated(dlg)
        return dlg
    }

    protected open fun onDialogCreated(dialog: Dialog) {

    }

    override fun onDestroy() {
        handler.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        handler.onSaveInstanceState(outState)
    }

    // -----------------------------
    // abstract functions
    // -----------------------------

    abstract override fun onHandleCreateBottomDialog(savedInstanceState: Bundle?): View

    // -----------------------------
    // Result
    // -----------------------------

    protected fun <X : BaseDialogEvent> sendEvent(event: X) {
        // send result to any custom handler
        DialogSetup.sendResult(event)
        // send result the default way
        DialogUtil.trySendResult(event, this, handler.customSendResultType
                ?: DialogSetup.DEFAULT_SEND_RESULT_TYPE)

        onEventSend(event)
    }

    protected open fun <X : BaseDialogEvent> onEventSend(event: X) {

    }
}