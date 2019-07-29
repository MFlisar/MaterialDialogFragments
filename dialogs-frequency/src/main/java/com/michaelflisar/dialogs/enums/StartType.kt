package com.michaelflisar.dialogs.enums

import com.michaelflisar.dialogs.CalendarUtil
import com.michaelflisar.dialogs.frequency.R

enum class StartType(val typeRes: Int) {
    Today(R.string.mdf_repeat_start_today),
    Tomorrow(R.string.mdf_repeat_start_tomorrow),
    NextMonday(R.string.mdf_repeat_start_next_monday),
    SelectDate(R.string.mdf_repeat_start_select_date);

    companion object {
        fun fromTimeInMillis(timeInMillis: Long) = when (timeInMillis) {
            CalendarUtil.today().timeInMillis -> Today
            CalendarUtil.tomorrow().timeInMillis -> Tomorrow
            CalendarUtil.nextMonday().timeInMillis -> NextMonday
            else -> SelectDate
        }
    }
}