package com.michaelflisar.dialogs.extension

import com.michaelflisar.dialogs.CalendarUtil
import java.util.*

internal fun Calendar.addDays(days: Int): Calendar {
    add(Calendar.DAY_OF_MONTH, days)
    return this
}

internal fun Calendar.addWeeks(weeks: Int): Calendar {
    add(Calendar.WEEK_OF_YEAR, weeks)
    return this
}

internal fun Calendar.addMonths(months: Int): Calendar {
    add(Calendar.MONTH, months)
    return this
}

internal fun Calendar.nextWeekDay(weekday: Int): Calendar {
    addDays(1)
    while (get(Calendar.DAY_OF_WEEK) != weekday) {
        addDays(1)
    }
    return this
}

internal fun Calendar.daysBetween(otherDate: Calendar, returnZeroForNegativeDifference: Boolean): Int {
    val smallerMillis = timeInMillis
    val largerMillis = otherDate.timeInMillis
    if (smallerMillis >= largerMillis && returnZeroForNegativeDifference) {
        return 0
    }
    return ((largerMillis - smallerMillis) / CalendarUtil.MILLIS_OF_DAY).toInt()
}

internal fun Calendar.weekday(): Int = get(Calendar.DAY_OF_WEEK)

internal fun Calendar.setWeekDay(day: Int): Calendar {
    set(Calendar.DAY_OF_WEEK, day)
    return this
}

internal fun Calendar.copy(): Calendar {
    return clone() as Calendar
}

internal fun Calendar.clearTime(): Calendar {
    apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return this
}