package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.CalendarUtil
import com.michaelflisar.dialogs.DialogFrequencySetup
import com.michaelflisar.dialogs.enums.*
import com.michaelflisar.dialogs.extension.addDays
import com.michaelflisar.dialogs.frequency.R
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Parcelize
class FrequencySetup(
    var unit: FrequencyUnit,
    var everyXUnit: Int = 1,
    var nTimesFactor: Int = 1,
    var repeatType: RepeatType = RepeatType.Regular,
    var weekDays: HashSet<WeekDay> = HashSet(),
    var monthDays: ArrayList<MonthDay> = ArrayList(),
    var startType: StartType = StartType.Today,
    var endType: EndType = EndType.Forever,
    var startDate: Calendar = CalendarUtil.today(),
    var endDate: Calendar = CalendarUtil.today().addDays(DialogFrequencySetup.DEFAULT_END_DAY_OFFSET),
    var endTimes: Int = DialogFrequencySetup.DEFAULT_END_TIMES
) : Parcelable, Serializable {

    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1
    }

    // --------------
    // calculate frequency
    // --------------

    fun calculateFrequency(context: Context): FrequencyResult {

        // 1) validate start/end timeInMillis
        val finalStartDate = when (startType) {
            StartType.Today -> CalendarUtil.today().timeInMillis
            StartType.Tomorrow -> CalendarUtil.tomorrow().timeInMillis
            StartType.NextMonday -> CalendarUtil.nextMonday().timeInMillis
            StartType.SelectDate -> startDate.timeInMillis
        }
        val finalEndData = when (endType) {
            EndType.Forever -> End.Never
            EndType.UntilDate -> {
                if (endDate < startDate) {
                    return FrequencyResult.Error(context.getString(R.string.mdf_error_end_is_before_start))
                } else End.Date(endDate.timeInMillis)
            }
            EndType.UntilTimes -> End.Times(endTimes)
        }

        // 2) depending on frequency type generate a frequency instance
        val frequency = when (unit) {
            FrequencyUnit.Day -> Frequency.Daily(everyXUnit, finalStartDate, finalEndData)
            FrequencyUnit.Week -> {
                when (repeatType) {
                    RepeatType.Regular -> {
                        if (weekDays.isEmpty()) {
                            return FrequencyResult.Error(context.getString(R.string.mdf_error_no_weekday_selected))
                        }
                        Frequency.Weekly(everyXUnit, weekDays, finalStartDate, finalEndData)
                    }
                    RepeatType.Irregular -> Frequency.NTimesWeekly(
                        everyXUnit,
                        nTimesFactor,
                        finalStartDate,
                        finalEndData
                    )
                }
            }
            FrequencyUnit.Month -> {
                when (repeatType) {
                    RepeatType.Regular -> {
                        if (monthDays.isEmpty()) {
                            return FrequencyResult.Error(context.getString(R.string.mdf_error_no_monthday_selected))
                        }
                        Frequency.Monthly(everyXUnit, monthDays, finalStartDate, finalEndData)
                    }
                    RepeatType.Irregular -> Frequency.NTimesMonthly(
                        everyXUnit,
                        nTimesFactor,
                        finalStartDate,
                        finalEndData
                    )
                }
            }
            FrequencyUnit.Year -> Frequency.Yearly(everyXUnit, finalStartDate, finalEndData)
        }

        return FrequencyResult.Success(frequency)
    }

    sealed class FrequencyResult {
        class Error(val error: String) : FrequencyResult()
        class Success(val frequency: Frequency<*>) : FrequencyResult()
    }
}