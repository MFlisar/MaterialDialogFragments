package com.michaelflisar.dialogs.enums

import android.os.Parcelable
import com.michaelflisar.dialogs.frequency.R
import kotlinx.parcelize.Parcelize

@Parcelize
internal enum class MonthDayType(val typeRes: Int) : Parcelable {
    DayOfMonth(R.string.mdf_month_day_type_day_of_month),
    DayOfWeek(R.string.mdf_month_day_type_weekday_of_month);
}