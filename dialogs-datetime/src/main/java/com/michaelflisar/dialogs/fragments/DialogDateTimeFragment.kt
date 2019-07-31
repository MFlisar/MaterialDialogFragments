package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.events.DialogDateTimeEvent
import com.michaelflisar.dialogs.events.DialogInfoEvent
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogDateTime
import com.michaelflisar.dialogs.title
import java.util.*

open class DialogDateTimeFragment : BaseDialogFragment<DialogDateTime>() {

    companion object {
        fun create(setup: DialogDateTime): DialogDateTimeFragment {
            val dlg = DialogDateTimeFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var date: Calendar

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("date")) {
                date = savedInstanceState.getSerializable("date") as Calendar
            }
        } else {
            date = setup.currentDateTime ?: Calendar.getInstance()
        }

        val dlg = MaterialDialog(activity!!)

        when (setup.type) {
            DialogDateTime.Type.DateOnly -> {
                dlg.datePicker(
                    setup.minDateTime,
                    setup.maxDateTime,
                    date,
                    setup.requireFutureDateTime
                ) { _, datetime ->
                    date = datetime
                }
            }
            DialogDateTime.Type.TimeOnly -> {
                dlg.timePicker(
                    date,
                    setup.requireFutureDateTime,
                    setup.show24HoursView
                ) { _, datetime ->
                    date = datetime
                }
            }
            DialogDateTime.Type.DateAndTime -> {
                dlg.dateTimePicker(
                    setup.minDateTime,
                    date,
                    setup.requireFutureDateTime,
                    setup.show24HoursView
                ) { _, datetime ->
                    date = datetime
                }
            }
        }
        dlg
            .positiveButton(setup.posButton) {
                sendEvent(
                    DialogDateTimeEvent(
                        setup,
                        WhichButton.POSITIVE.ordinal,
                        DialogDateTimeEvent.Data(date)
                    )
                )
                dismiss()
            }
            .cancelable(setup.cancelable)
        this.isCancelable = setup.cancelable


        setup.title?.let {
            dlg.title(it)
        }

        setup.negButton?.let {
            dlg.negativeButton(it) {
                sendEvent(
                    DialogDateTimeEvent(
                        setup,
                        WhichButton.NEGATIVE.ordinal,
                        null
                    )
                )
                dismiss()
            }
        }

        setup.neutrButton?.let {
            dlg.neutralButton(it) {
                sendEvent(
                    DialogDateTimeEvent(
                        setup,
                        WhichButton.NEUTRAL.ordinal,
                        null
                    )
                )
            }
        }

        return dlg
    }

    private fun onClick(which: WhichButton) {
        sendEvent(DialogInfoEvent(setup, which.index))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("date", date)
    }

}
