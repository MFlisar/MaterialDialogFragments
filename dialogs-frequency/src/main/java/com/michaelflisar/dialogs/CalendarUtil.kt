package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.extension.addDays
import com.michaelflisar.dialogs.extension.clearTime
import com.michaelflisar.dialogs.extension.nextWeekDay
import java.text.DateFormatSymbols
import java.util.*

internal object CalendarUtil {

    val WEEKDAYS: List<String> by lazy {
        val symbols = DateFormatSymbols()
        symbols.shortWeekdays.map { it.replace(".", "") }
    }

    const val MILLIS_OF_DAY = 1000 * 60 * 60 * 24

    fun today(): Calendar {
        val c = Calendar.getInstance()
        c.clearTime()
        return c
    }

    fun tomorrow(): Calendar = today().addDays(1)

    fun nextMonday(): Calendar = today().nextWeekDay(Calendar.MONDAY)
}