package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.text.Text
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.events.DialogProgressEvent
import com.michaelflisar.dialogs.helper.EventQueue
import com.michaelflisar.dialogs.interfaces.IProgressDialogFragment
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.setups.DialogProgress
import com.michaelflisar.dialogs.textView

class DialogProgressFragment : MaterialDialogFragment<DialogProgress>(), IProgressDialogFragment {

    companion object {
        fun create(setup: DialogProgress): DialogProgressFragment {
            val dlg = DialogProgressFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private var text: String? = null

    private val eventQueue: EventQueue

    init {
        eventQueue = object : EventQueue(true) {
            override fun onEventDeliveration(event: Any) {
                if (event is Event) {
                    when (event) {
                        is Event.Update -> {
                            text = event.text.get(activity!!)
                            updateText()
                        }
                        is Event.Close -> {
                            if (event.forcedByNewDialog) {
                                sendEvent(DialogProgressEvent(setup, null, DialogProgressEvent.Data(true, true)))
                            } else {
                                sendEvent(DialogProgressEvent(setup, null, DialogProgressEvent.Data(true, false)))
                            }
                            dismissAllowingStateLoss()
                        }
                    }
                }
            }
        }

        DialogProgress.setDialog(this)
    }

    override fun onResume() {
        super.onResume()
        eventQueue.onResume()
    }

    override fun onPause() {
        eventQueue.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        eventQueue.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (text != null) {
            outState.putString("text", text)
        }
    }

    override fun update(event: Text) {
        eventQueue.handleEvent(Event.Update(event))
    }

    override fun close(forcedByNewDialog: Boolean) {
        eventQueue.handleEvent(Event.Close(false))
    }

    // -----------------------------
    // public static updater
    // -----------------------------

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        text = if (savedInstanceState != null && savedInstanceState.containsKey("text")) {
            savedInstanceState.getString("text")
        } else {
            setup.text?.get(activity!!)
        }

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(activity!!, this)

        if (text != null)
            dialog.message(text = text.toString())

        dialog.customView(if (setup.horizontal) R.layout.dialog_progress_horizontal else R.layout.dialog_progress)
                .cancelable(false)
                .noAutoDismiss()
                .negativeButton(setup) {
                    sendEvent(DialogProgressEvent(setup, WhichButton.NEGATIVE.ordinal, DialogProgressEvent.Data(setup.dismissOnNegative, false)))
                    if (setup.dismissOnNegative) {
                        dismissAllowingStateLoss()
                    }
                }
                .neutralButton(setup) {
                    sendEvent(DialogProgressEvent(setup, WhichButton.NEUTRAL.ordinal, null))
                }

        return dialog
    }

    private fun updateText() {
        val dialog = dialog as? MaterialDialog
        dialog?.textView()?.text = text
    }

    internal sealed class Event {
        class Update(val text: Text) : Event()
        class Close(val forcedByNewDialog: Boolean = false) : Event()
    }
}
