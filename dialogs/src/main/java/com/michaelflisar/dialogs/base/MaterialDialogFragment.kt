package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.SendResultType
import com.michaelflisar.dialogs.classes.SimpleBaseDialogSetup
import com.michaelflisar.dialogs.events.MaterialDialogEvent
import com.michaelflisar.dialogs.events.DialogCancelledEvent
import com.michaelflisar.dialogs.utils.DialogUtil

abstract class MaterialDialogFragment<T : SimpleBaseDialogSetup> : DialogFragment() {

    companion object {
        const val ARG_SETUP = "setup"
    }

    // -----------
    // Variables
    // -----------

    var customSendResultType: SendResultType? = null
        internal set
    private var pSetup: T? = null
    protected val setup: T
        get() {
            if (pSetup == null) {
                pSetup = requireArguments().getParcelable(ARG_SETUP)!!
            }
            return pSetup!!
        }

    // -----------
    // Functions - Setup
    // -----------

    protected fun resetSetup() {
        pSetup = null
    }

    fun setSetupArgs(setup: SimpleBaseDialogSetup) {
        val args = arguments ?: Bundle()
        args.putParcelable(ARG_SETUP, setup)
        arguments = args
        resetSetup()
    }

    // -----------
    // Dialog Events
    // -----------

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dlg: Dialog = onHandleCreateDialog(savedInstanceState)
        onViewReady(dlg, null)
        return dlg
    }

    private fun onViewReady(dlg: Dialog, view: View?) {
        if (this is IDialogReadyListener) {
            (this as IDialogReadyListener).onDialogReady(dlg, view)
        }
        if (activity is IDialogReadyListener) {
            (activity as IDialogReadyListener).onDialogReady(dlg, view)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        if (setup.sendCancelEvent) {
            sendEvent(DialogCancelledEvent(setup))
        }
        super.onCancel(dialog)
    }

    // -----------------------------
    // Result
    // -----------------------------

    protected fun <X : MaterialDialogEvent> sendEvent(event: X) {
        // send result to any custom handler
        DialogSetup.sendResult(event, this)
        // send result the default way
        DialogUtil.trySendResult(
                event,
                this,
                this.customSendResultType ?: DialogSetup.DEFAULT_SEND_RESULT_TYPE
        )

        onEventSend(event)
    }

    protected open fun <X : MaterialDialogEvent> onEventSend(event: X) {

    }

    // -----------------------------
    // abstract functions
    // -----------------------------

    abstract fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog

    // -----------------------------
    // Interface
    // -----------------------------

    interface IDialogReadyListener {
        fun onDialogReady(dialog: Dialog?, view: View?)
    }
}