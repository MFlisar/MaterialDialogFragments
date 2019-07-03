package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.frequency.R

enum class StartType(val typeRes: Int) {
    Today(R.string.dialogs_repeat_start_today),
    Tomorrow(R.string.dialogs_repeat_start_tomorrow),
    NextMonday(R.string.dialogs_repeat_start_next_monday),
    SelectDate(R.string.dialogs_repeat_start_select_date)
}