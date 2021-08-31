package com.michaelflisar.dialogs.enums

import android.os.Parcelable
import com.michaelflisar.dialogs.frequency.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EndType(val typeRes: Int) : Parcelable {
    Forever(R.string.mdf_repeat_end_forever),
    UntilDate(R.string.mdf_repeat_end_select_date),
    UntilTimes(R.string.mdf_repeat_end_number_of_times)
}