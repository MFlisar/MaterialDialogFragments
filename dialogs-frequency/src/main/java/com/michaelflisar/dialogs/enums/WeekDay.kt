package com.michaelflisar.dialogs.enums

import android.os.Parcelable
import com.michaelflisar.dialogs.DialogFrequencySetup
import com.michaelflisar.dialogs.extension.setWeekDay
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
enum class WeekDay(
    val calendarDay: Int,
    val orderMondayBased: Int,
    val orderSundayBased: Int
) : Parcelable {
    Monday(Calendar.MONDAY, 0, 1),
    Tuesday(Calendar.TUESDAY, 1, 2),
    Wednesday(Calendar.WEDNESDAY, 2, 3),
    Thursday(Calendar.THURSDAY, 3, 4),
    Friday(Calendar.FRIDAY, 4, 5),
    Saturday(Calendar.SATURDAY, 5, 6),
    Sunday(Calendar.SUNDAY, 6, 0);

    companion object {
        fun sorted(): List<WeekDay> {
            val d = ArrayList(values().toList())
            if (DialogFrequencySetup.FIRST_DAY_OF_WEEK_IS_MONDAY) {
                d.sortBy { it.orderMondayBased }
            } else {
                d.sortBy { it.orderSundayBased }
            }
            return d
        }

        fun firstDayOfWeek() = if (DialogFrequencySetup.FIRST_DAY_OF_WEEK_IS_MONDAY) {
            Monday
        } else {
            Sunday
        }
    }

    fun userSelectedOrderBase() = if (DialogFrequencySetup.FIRST_DAY_OF_WEEK_IS_MONDAY) {
        orderMondayBased
    } else {
        orderSundayBased
    }

    fun shortName() = Calendar.getInstance().setWeekDay(calendarDay).getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.SHORT,
        Locale.getDefault()
    )

    fun longName() = Calendar.getInstance().setWeekDay(calendarDay).getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.LONG,
        Locale.getDefault()
    )
}