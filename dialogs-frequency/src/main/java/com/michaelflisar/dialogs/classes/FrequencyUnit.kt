package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.frequency.R

enum class FrequencyUnit(
    val labelTypeRes: Int,
    val labelBeforeEveryXTime: Int,
    val labelAfterEveryXTime: Int
) {
    Day(
        R.string.dialogs_type_repeat_daily,
        R.string.before_every_x_day,
        R.string.after_every_x_day
    ),
    Week(
        R.string.dialogs_type_repeat_weekly,
        R.string.before_every_x_week,
        R.string.after_every_x_week
    ),
    Month(
        R.string.dialogs_type_repeat_monthly,
        R.string.before_every_x_month,
        R.string.after_every_x_month
    ),
    Year(
        R.string.dialogs_type_repeat_yearly,
        R.string.before_every_x_year,
        R.string.after_every_x_year
    )
}