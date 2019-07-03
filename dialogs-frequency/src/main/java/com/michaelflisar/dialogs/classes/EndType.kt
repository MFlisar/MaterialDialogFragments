package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.frequency.R

enum class EndType(val typeRes: Int) {
    Forever(R.string.dialogs_repeat_end_forever),
    UntilDate(R.string.dialogs_repeat_end_select_date),
    UntilTimes(R.string.dialogs_repeat_end_number_of_times)
}