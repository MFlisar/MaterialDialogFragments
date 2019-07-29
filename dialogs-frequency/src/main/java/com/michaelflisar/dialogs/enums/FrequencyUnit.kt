package com.michaelflisar.dialogs.enums

import com.michaelflisar.dialogs.frequency.R

enum class FrequencyUnit(
    val labelTypeRes: Int,
    val labelBeforeEveryXTime: Int,
    val labelAfterEveryXTime: Int,
    val supportIrregularRepeat: Boolean,
    val labelBeforeNTimes: Int,
    val labelAfterNTimes: Int
) {
    Day(
        R.string.mdf_type_repeat_daily,
        R.string.mdf_before_every_x_day,
        R.string.mdf_after_every_x_day,
        false,
        -1,
        -1
    ),
    Week(
        R.string.mdf_type_repeat_weekly,
        R.string.mdf_before_every_x_week,
        R.string.mdf_after_every_x_week,
        true,
        R.string.mdf_before_n_times_week,
        R.string.mdf_after_n_times_week
    ),
    Month(
        R.string.mdf_type_repeat_monthly,
        R.string.mdf_before_every_x_month,
        R.string.mdf_after_every_x_month,
        true,
        R.string.mdf_before_n_times_month,
        R.string.mdf_after_n_times_month
    ),
    Year(
        R.string.mdf_type_repeat_yearly,
        R.string.mdf_before_every_x_year,
        R.string.mdf_after_every_x_year,
        false,
        -1,
        -1
    )
}