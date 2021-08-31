package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import com.afollestad.date.month
import com.afollestad.date.year
import com.michaelflisar.dialogs.CalendarUtil
import com.michaelflisar.dialogs.DialogFrequencySetup
import com.michaelflisar.dialogs.enums.FrequencyUnit
import com.michaelflisar.dialogs.enums.RepeatType
import com.michaelflisar.dialogs.enums.StartType
import com.michaelflisar.dialogs.enums.WeekDay
import com.michaelflisar.dialogs.extension.*
import com.michaelflisar.dialogs.frequency.R
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

sealed class Frequency<T> : Parcelable {

    companion object {
        fun formatMillis(timeInMillis: Long) = DialogFrequencySetup.FREQUENCY_DATE_FORMAT.format(Date(timeInMillis))
    }

    abstract var startTimeInMillis: Long
    abstract var end: End

    //abstract fun calcDays(limit: Int = 100, minDate: Calendar? = null): List<Calendar>
    abstract fun toReadableString(
        context: Context,
        includeBeginning: Boolean = true,
        includeEnd: Boolean = true
    ): String

    fun getReadableStringStartInfo(context: Context): String {
        return context.getString(R.string.mdf_formatted_added_start_date, formatMillis(startTimeInMillis))
    }

    fun getReadableStringEndInfo(context: Context): String {
        return when (end) {
            End.Never -> context.getString(R.string.mdf_formatted_added_end_never)
            is End.Times -> context.getString(R.string.mdf_formatted_added_end_times, (end as End.Times).times)
            is End.Date -> context.getString(
                R.string.mdf_formatted_added_end_date,
                formatMillis((end as End.Date).timeInMillis)
            )
        }
    }

    abstract fun toSetup(alwaysConvertDates: Boolean = true): FrequencySetup

    abstract fun calcDays(limit: Int? = null, minDate: Calendar? = null, data: T? = null): List<Calendar>

    @Parcelize
    class Daily(
        var everyXDays: Int = 1,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<Unit>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: Unit?): List<Calendar> {
            val list = ArrayList<Calendar>()
            val startDate = CalendarUtil.convertMillis(startTimeInMillis)
            // 1) calc days we have to add to startTimeInMillis timeInMillis to reach min timeInMillis
            val diffToMinDate = diffToMinDate(startDate, minDate)
            // 2) calc first valid after min timeInMillis (depending on everyXUnit)
            val daysToAddAtLeast = ceil(diffToMinDate.toFloat() / everyXDays.toFloat()).toInt() * everyXDays
            // 3) calc all days until limit is reached or end timeInMillis is reached
            var date: Calendar = startDate.copy().addDays(daysToAddAtLeast)
            while ((limit == null || list.size < limit) && validEndDate(end, date)) {
                list.add(date.copy())
                date = date.addDays(everyXDays)
            }
            return list
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val s = when (everyXDays) {
                1 -> context.getString(R.string.mdf_formatted_frequency_day_1)
                2 -> context.getString(R.string.mdf_formatted_frequency_day_2)
                else -> context.getString(R.string.mdf_formatted_frequency_day_X, everyXDays)
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Day,
            everyXUnit = everyXDays,
            repeatType = RepeatType.Regular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    @Parcelize
    class Weekly(
        var everyXWeeks: Int = 1,
        /*
         * zero based week day index, starting at 0 for monday until 6 for sunday
         */
        var days: Set<WeekDay>,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<Unit>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: Unit?): List<Calendar> {

            var list = ArrayList<Calendar>()

            // 1) prepare data
            val startDate = CalendarUtil.convertMillis(startTimeInMillis)
            val sortedDays = sortedDays(days, true)  // always sort by monday because CW starts at monday as well!!!
            val diffToMinDate = diffToMinDate(startDate, minDate)
            val minimum = startDate.copy().addDays(diffToMinDate)

            // 2) define check function for a given week
            fun checkWeek(dateOfWeek: Calendar): Boolean {
                sortedDays.forEach {
                    dateOfWeek.setWeekDay(it.calendarDay)
                    if (!validEndDate(end, dateOfWeek)) {
                        return true
                    }
                    if ((limit == null || list.size < limit) && dateOfWeek.timeInMillis >= minimum.timeInMillis) {
                        list.add(dateOfWeek.copy())
                    }
                }
                return false
            }

            // 3) start calculation
            // 3.1) we must calculate all results in this case to filter out by limit
            if (end is End.Times) {

                // get all dates in all valid weeks + remove invalid days of last week
                val date = startDate.copy()
                while (list.size < (end as End.Times).times) {
                    checkWeek(date)
                    date.addWeeks(everyXWeeks)
                }
                if (list.size > (end as End.Times).times) {
                    list = ArrayList(list.take((end as End.Times).times))
                }

                // remove invalid dates for given minDate
                val firstValidIndex = minDate?.let { minDate -> list.indexOfFirst { it >= minDate } } ?: 0
                if (firstValidIndex == -1) {
                    list.clear()
                } else if (firstValidIndex > 0) {
                    list = ArrayList(list.subList(firstValidIndex, list.size))
                }

                return limit?.let { ArrayList(list.take(it)) } ?: list
            }
            // 3.2) otherwise we can optimise calculation and start calculation at correct position
            else {

                val date = minimum.copy()
                var endDateReached = false
                while ((limit == null || list.size < limit) && !endDateReached) {
                    endDateReached = checkWeek(date)
                    date.addWeeks(everyXWeeks)
                }

            }

            return list
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val separator = context.getString(R.string.mdf_formatted_list_separator)
            val lastSeparator = context.getString(R.string.mdf_formatted_last_list_separator)
            val s = when (everyXWeeks) {
                1 -> context.getString(
                    R.string.mdf_formatted_frequency_week_1,
                    printDaysSorted(days, separator, lastSeparator)
                )
                2 -> context.getString(
                    R.string.mdf_formatted_frequency_week_2,
                    printDaysSorted(days, separator, lastSeparator)
                )
                else -> context.getString(
                    R.string.mdf_formatted_frequency_week_X,
                    everyXWeeks,
                    printDaysSorted(days, separator, lastSeparator)
                )
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Week,
            everyXUnit = everyXWeeks,
            weekDays = days.toHashSet(),
            repeatType = RepeatType.Regular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    @Parcelize
    class NTimesWeekly(
        var everyXWeeks: Int = 1,
        var nTimesFactor: Int = 1,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<Set<WeekDay>>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: Set<WeekDay>?): List<Calendar> {

            if (data?.size != nTimesFactor) {
                throw RuntimeException("data.size != nTimesFactor, this is not allowed!")
            }

            val weekly = Weekly(
                everyXWeeks,
                data,
                startTimeInMillis,
                end
            )

            return weekly.calcDays(limit, minDate, Unit)
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val s = when (everyXWeeks) {
                1 -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_week_1_ntimes_1)
                    else -> context.getString(R.string.mdf_formatted_frequency_week_1_ntimes_X, nTimesFactor)
                }
                2 -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_week_2_ntimes_1)
                    else -> context.getString(R.string.mdf_formatted_frequency_week_2_ntimes_X, nTimesFactor)
                }
                else -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_week_X_ntimes_1, everyXWeeks)
                    else -> context.getString(
                        R.string.mdf_formatted_frequency_week_X_ntimes_X,
                        everyXWeeks,
                        nTimesFactor
                    )
                }
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Week,
            everyXUnit = everyXWeeks,
            nTimesFactor = nTimesFactor,
            repeatType = RepeatType.Irregular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    @Parcelize
    class Monthly(
        var everyXMonths: Int = 1,
        /*
        * zero based day of month index
        */
        var days: List<MonthDay>,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<Unit>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: Unit?): List<Calendar> {
            var list = ArrayList<Calendar>()

            // 1) prepare data
            val startDate1 = CalendarUtil.convertMillis(startTimeInMillis)
            val diffToMinDate = diffToMinDate(startDate1, minDate)
            val minimum = startDate1.copy().addDays(diffToMinDate)
            val firstDayOfStartDateMonth = startDate1.apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }

            // 2) define check function for a given month
            fun checkMonth(year: Int, month: Int): Boolean {
                val checkedDates = hashSetOf<Calendar>()
                val internalList = ArrayList<Calendar>()
                var invalidDateFound = false
                days.forEach {

                    // we make sure to chekc each date once only (first monday and day 1 of a month may be the same day e.g. and so on...)
                    val dateOfMonth = it.calcDate(year, month)
                    if (!checkedDates.contains(dateOfMonth)) {
                        checkedDates.add(dateOfMonth)

                        if (!validEndDate(end, dateOfMonth)) {
                            invalidDateFound = true
                            return@forEach
                        }

                        if ((limit == null || (list.size + internalList.size) < limit) && dateOfMonth.timeInMillis >= minimum.timeInMillis) {
                            internalList.add(dateOfMonth.copy())
                        }
                    }

                }
                internalList.sort()
                list.addAll(internalList)
                return invalidDateFound
            }

            // 3) start calculation
            // 3.1) we must calculate all results in this case to filter out by limit
            if (end is End.Times) {

                // get all dates in all valid months + remove invalid days of first and last month
                val date = firstDayOfStartDateMonth.copy()
                while (list.size < (end as End.Times).times) {
                    checkMonth(date.year, date.month)
                    date.addMonths(everyXMonths)
                }
                if (list.size > (end as End.Times).times) {
                    list = ArrayList(list.take((end as End.Times).times))
                }

                // remove invalid dates for given minDate
                val firstValidIndex = minDate?.let { minDate -> list.indexOfFirst { it >= minDate } } ?: 0
                if (firstValidIndex == -1) {
                    list.clear()
                } else if (firstValidIndex > 0) {
                    list = ArrayList(list.subList(firstValidIndex, list.size))
                }

                return limit?.let { ArrayList(list.take(it)) } ?: list
            }
            // 3.2) otherwise we can optimise calculation and start calculation at correct position
            else {

                val date = minimum.copy()
                var endDateReached = false
                while ((limit == null || list.size < limit) && !endDateReached) {
                    endDateReached = checkMonth(date.year, date.month)
                    date.addMonths(everyXMonths)
                }

            }

            return list
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val separator = context.getString(R.string.mdf_formatted_list_separator)
            val lastSeparator = context.getString(R.string.mdf_formatted_last_list_separator)
            val s = when (everyXMonths) {
                1 -> context.getString(
                    R.string.mdf_formatted_frequency_month_1,
                    printMonthDaysSorted(context, days, separator, lastSeparator)
                )
                2 -> context.getString(
                    R.string.mdf_formatted_frequency_month_2,
                    printMonthDaysSorted(context, days, separator, lastSeparator)
                )
                else -> context.getString(
                    R.string.mdf_formatted_frequency_month_X,
                    everyXMonths,
                    printMonthDaysSorted(context, days, separator, lastSeparator)
                )
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Month,
            everyXUnit = everyXMonths,
            monthDays = ArrayList(days),
            repeatType = RepeatType.Regular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    @Parcelize
    class NTimesMonthly(
        var everyXMonths: Int = 1,
        var nTimesFactor: Int = 1,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<List<MonthDay>>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: List<MonthDay>?): List<Calendar> {

            if (data?.size != nTimesFactor) {
                throw RuntimeException("data.size != nTimesFactor, this is not allowed!")
            }

            val monthly = Monthly(
                everyXMonths,
                data,
                startTimeInMillis,
                end
            )

            return monthly.calcDays(limit, minDate, Unit)
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val s = when (everyXMonths) {
                1 -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_month_1_ntimes_1)
                    else -> context.getString(R.string.mdf_formatted_frequency_month_1_ntimes_X, nTimesFactor)
                }
                2 -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_month_2_ntimes_1)
                    else -> context.getString(R.string.mdf_formatted_frequency_month_2_ntimes_X, nTimesFactor)
                }
                else -> when (nTimesFactor) {
                    1 -> context.getString(R.string.mdf_formatted_frequency_month_X_ntimes_1, everyXMonths)
                    else -> context.getString(
                        R.string.mdf_formatted_frequency_month_X_ntimes_X,
                        everyXMonths,
                        nTimesFactor
                    )
                }
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Month,
            everyXUnit = everyXMonths,
            nTimesFactor = nTimesFactor,
            repeatType = RepeatType.Irregular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    @Parcelize
    class Yearly(
        var everyXYears: Int = 1,
        override var startTimeInMillis: Long = CalendarUtil.today().timeInMillis,
        override var end: End = End.Never
    ) : Frequency<Unit>() {

        override fun calcDays(limit: Int?, minDate: Calendar?, data: Unit?): List<Calendar> {
            TODO()
        }

        override fun toReadableString(context: Context, includeBeginning: Boolean, includeEnd: Boolean): String {
            val s = when (everyXYears) {
                1 -> context.getString(R.string.mdf_formatted_frequency_year_1)
                2 -> context.getString(R.string.mdf_formatted_frequency_year_2)
                else -> context.getString(R.string.mdf_formatted_frequency_year_X, everyXYears)
            }
            return addInfos(context, s, includeBeginning, includeEnd)
        }

        override fun toSetup(alwaysConvertDates: Boolean) = FrequencySetup(
            unit = FrequencyUnit.Year,
            everyXUnit = everyXYears,
            repeatType = RepeatType.Regular,
            startType = calcStartType(alwaysConvertDates, startTimeInMillis),
            startDate = CalendarUtil.convertMillis(startTimeInMillis),
            endType = end.endType,
            endDate = calcEndDate(),
            endTimes = calcEndTimes()
        )
    }

    // -----------------
    // helper functions
    // -----------------

    internal fun diffToMinDate(startDate: Calendar, minDate: Calendar?) =
        minDate?.let { startDate.daysBetween(it, true) } ?: 0

    internal fun validEndDate(endType: End, date: Calendar): Boolean {
        return when (endType) {
            End.Never -> true
            is End.Times -> true
            is End.Date -> return date.timeInMillis < endType.timeInMillis
        }
    }

    internal fun calcStartType(alwaysConvertDates: Boolean, startTimeInMillis: Long): StartType {
        if (!alwaysConvertDates)
            return StartType.SelectDate
        return StartType.fromTimeInMillis(startTimeInMillis)
    }

    internal fun calcEndDate(): Calendar {
        return (end as? End.Date)?.timeInMillis?.let { CalendarUtil.convertMillis(it) } ?: CalendarUtil.convertMillis(
            startTimeInMillis
        ).addDays(DialogFrequencySetup.DEFAULT_END_DAY_OFFSET)
    }

    internal fun calcEndTimes(): Int {
        return (end as? End.Times)?.times ?: DialogFrequencySetup.DEFAULT_END_TIMES
    }

    internal fun addInfos(context: Context, s: String, includeBeginning: Boolean, includeEnd: Boolean): String {
        var res = s
        if (includeBeginning) {
            res += context.getString(R.string.mdf_formatted_separator) + getReadableStringStartInfo(context)
        }
        if (includeEnd) {
            res += context.getString(R.string.mdf_formatted_separator) + getReadableStringEndInfo(context)
        }
        return res
    }

    internal fun sortedDays(
        days: Set<WeekDay>,
        mondayIsFirstDate: Boolean = DialogFrequencySetup.FIRST_DAY_OF_WEEK_IS_MONDAY
    ): ArrayList<WeekDay> {
        val d = ArrayList(days)
        if (mondayIsFirstDate) {
            d.sortBy { it.orderMondayBased }
        } else {
            d.sortBy { it.orderSundayBased }
        }
        return d
    }

    internal fun printDaysSorted(days: Set<WeekDay>, separator: String, lastSeparator: String): String {
        val d = sortedDays(days)
        var joined = d.map { it.shortName() }.joinToString(separator = separator)
        return replaceLastComma(joined, separator, lastSeparator)
    }

    internal fun printMonthDaysSorted(
        context: Context,
        days: List<MonthDay>,
        separator: String,
        lastSeparator: String
    ): String {
        val d = ArrayList(days)
        d.sortWith(MonthDay.SORTED)
        var joined = d.map { it.toReadableString(context, true) }.joinToString(separator = separator)
        return replaceLastComma(joined, separator, lastSeparator)
    }

    internal fun replaceLastComma(s: String, separator: String, lastSeparator: String): String {
        var res = s
        val signToReplace = separator
        var lastCommaIndex = res.lastIndexOf(signToReplace)
        if (lastCommaIndex >= 0) {
            res = res.substring(
                0,
                lastCommaIndex
            ) + lastSeparator + res.substring(lastCommaIndex + signToReplace.length)
        }
        return res
    }
}