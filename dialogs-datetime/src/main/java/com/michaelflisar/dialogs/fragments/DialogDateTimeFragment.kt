package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.events.DialogDateTimeEvent
import com.michaelflisar.dialogs.events.DialogInfoEvent
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogDateTime
import java.util.*

open class DialogDateTimeFragment : MaterialDialogFragment<DialogDateTime>() {

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

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(activity!!, this)

        when (setup.type) {
            DialogDateTime.Type.DateOnly -> {
                dialog.datePicker(
                        setup.minDateTime,
                        setup.maxDateTime,
                        date,
                        setup.requireFutureDateTime
                ) { _, datetime ->
                    date = datetime
                }
            }
            DialogDateTime.Type.TimeOnly -> {
                dialog.timePicker(
                        date,
                        setup.requireFutureDateTime,
                        setup.show24HoursView
                ) { _, datetime ->
                    date = datetime
                }
            }
            DialogDateTime.Type.DateAndTime -> {
                dialog.dateTimePicker(
                        setup.minDateTime,
                        date,
                        setup.requireFutureDateTime,
                        setup.show24HoursView
                ) { _, datetime ->
                    date = datetime
                }
            }
        }

        dialog
                .positiveButton(setup) {
                    sendEvent(
                            DialogDateTimeEvent(
                                    setup,
                                    WhichButton.POSITIVE.ordinal,
                                    DialogDateTimeEvent.Data(date)
                            )
                    )
                    dismiss()
                }.negativeButton(setup) {
                    sendEvent(
                            DialogDateTimeEvent(
                                    setup,
                                    WhichButton.NEGATIVE.ordinal,
                                    null
                            )
                    )
                    dismiss()
                }
                .neutralButton(setup) {
                    sendEvent(
                            DialogDateTimeEvent(
                                    setup,
                                    WhichButton.NEUTRAL.ordinal,
                                    null
                            )
                    )
                }

        return dialog
    }

    private fun onClick(which: WhichButton) {
        sendEvent(DialogInfoEvent(setup, which.index))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("date", date)
    }

}
