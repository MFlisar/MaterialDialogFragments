package com.michaelflisar.dialogs

import com.afollestad.date.month
import com.michaelflisar.dialogs.enums.WeekDay
import com.michaelflisar.dialogs.extension.*
import com.michaelflisar.dialogs.extension.addDays
import com.michaelflisar.dialogs.extension.clearTime
import com.michaelflisar.dialogs.extension.nextWeekDay
import com.michaelflisar.dialogs.extension.weekday
import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.max
import kotlin.math.min

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

    fun convertMillis(timeInMillisToSet: Long) : Calendar = Calendar.getInstance().apply { timeInMillis = timeInMillisToSet }

    fun monthDayOfYear(year: Int, month: Int, day: Int, dayFromBeginning: Boolean = true): Calendar {
        val c = Calendar.getInstance()
        c.clearTime()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        val maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (dayFromBeginning) {
            c.set(Calendar.DAY_OF_MONTH, min(day, maxDay))
        } else {
            c.set(Calendar.DAY_OF_MONTH, max(1, maxDay - day))
        }
        return c
    }

    fun weekDayOfMonth(year: Int, month: Int, weekDay: WeekDay, number: Int): Calendar {
        val c = Calendar.getInstance()
        c.clearTime()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, 1)

        val firstCorrectDayInMonth = c
        while (firstCorrectDayInMonth.weekday() != weekDay.calendarDay) {
            firstCorrectDayInMonth.addDays(1)
        }

        val desiredDay = firstCorrectDayInMonth.copy().addWeeks(number)
        while (desiredDay.month != month)  {
            desiredDay.addWeeks(-1)
        }

        return desiredDay
    }
}