package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.CalendarUtil
import com.michaelflisar.dialogs.extension.*
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

@Parcelize
class FrequencySetup(
    var unit: FrequencyUnit,
    var everyXUnit: Int = 1,
    var numberOfTimes: Int = 1,
    var repeatType: RepeatType = RepeatType.Regular,
    var weedays: WeekDays = WeekDays(),
    var startType: StartType = StartType.Today,
    var endType: EndType = EndType.Forever,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var endTimes: Int? = null
) : Parcelable, Serializable {

    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1
    }

    private var finished: Boolean = false

    @Transient
    private var startDateCalendar: Calendar? = null

    @Transient
    private var endDateCalendar: Calendar? = null

    // --------------
    // functions
    // --------------

    fun valid() {
        // TODO
    }

    fun finish() {

        // we must convert start type / end type to days
        when (startType) {
            StartType.Today -> startDate = CalendarUtil.today().timeInMillis
            StartType.Tomorrow -> startDate = CalendarUtil.tomorrow().timeInMillis
            StartType.NextMonday -> startDate = CalendarUtil.nextMonday().timeInMillis
            StartType.SelectDate -> {
                if (startDate == null) {
                    throw RuntimeException("No start day provided!")
                }
            }
        }

        when (endType) {
            EndType.Forever -> {
                endDate = null
                endTimes = null
            }
            EndType.UntilDate -> {
                endTimes = null
                if (endDate == null) {
                    throw RuntimeException("No end day provided!")
                }
            }
            EndType.UntilTimes -> {
                endDate = null
                if (endTimes == null) {
                    throw RuntimeException("No end times provided!")
                }
            }
        }

        when (unit) {
            FrequencyUnit.Day -> {

            }
            FrequencyUnit.Week -> {
                if (weedays.asCalendarWeekDays().isEmpty()) {
                    throw RuntimeException("No week days provided for weekly repeation!")
                }
            }
            FrequencyUnit.Month -> TODO()
            FrequencyUnit.Year -> TODO()
        }

        finished = true
    }

    fun calcDays(limit: Int = 100, minDate: Calendar? = null): List<Calendar> {

        minDate?.clearTime()

        val days = when (unit) {
            FrequencyUnit.Day -> calcDayDates(limit, minDate)
            FrequencyUnit.Week -> calcWeekDates(limit, minDate)
            FrequencyUnit.Month -> TODO()
            FrequencyUnit.Year -> TODO()
        }

        return days
    }

    private fun calcDayDates(limit: Int, minDate: Calendar?): List<Calendar> {
        val list = ArrayList<Calendar>()

        when (repeatType) {
            RepeatType.Regular -> {
                // 1) calc days we have to add to start date to reach min date
                val diffToMinDate = diffToMinDate(minDate)
                // 2) calc first valid after min date (depending on everyXUnit)
                val daysToAddAtLeast = ceil(diffToMinDate.toDouble() / everyXUnit.toDouble()).toInt()
                // 3) calc all days until limit is reached or end date is reached
                var date: Calendar = getStartDate().copy().addDays(daysToAddAtLeast)
                while (list.size < limit && validEndDate(date)) {
                    list.add(date.copy())
                    date = date.addDays(everyXUnit)
                }
            }
            RepeatType.Irregular -> throw RuntimeException("Irregular repeat types with freqeuncy unit == day is not supported!")
        }

        return list
    }

    private fun calcWeekDates(limit: Int, minDate: Calendar?): List<Calendar> {
        val list = ArrayList<Calendar>()

        when (repeatType) {
            RepeatType.Regular -> {
                // 1) calc days we have to add to start date to reach min date
                val diffToMinDate = diffToMinDate(minDate)
                // 2) get all valid week days
                val calendarWeekDays = weedays.asCalendarWeekDays()
                // 3) calc first date + define function to process single week
                val minimum = getStartDate().copy().addDays(diffToMinDate)
                fun checkWeek(dateOfWeek: Calendar, checkMinimum: Boolean): Boolean {
                    calendarWeekDays.forEach {
                        dateOfWeek.setWeekDay(it)
                        if (!validEndDate(dateOfWeek)) {
                            return true
                        }
                        if (checkMinimum && dateOfWeek.timeInMillis >= minimum.timeInMillis) {
                            list.add(dateOfWeek.copy())
                        }
                    }
                    return false
                }

                // 4) check week of minimum date and then check next valid week
                val date = minimum.copy()
                var endDateReached = checkWeek(date, true)
                while (list.size < limit && !endDateReached) {
                    date.addWeeks(everyXUnit)
                    endDateReached = checkWeek(date, true)
                }
            }
            RepeatType.Irregular -> {
                // TODO...
            }
        }

        return list
    }

    // --------------
    // helper functions
    // --------------

    private fun diffToMinDate(minDate: Calendar?) = minDate?.daysBetween(getStartDate(), true) ?: 0

    private fun validEndDate(date: Calendar): Boolean {
        return when (endType) {
            EndType.Forever -> true
            EndType.UntilDate -> return date.timeInMillis < getEndDate()!!.timeInMillis
            EndType.UntilTimes -> true
        }
    }

    private fun getStartDate(): Calendar {
        if (startDateCalendar == null) {
            startDateCalendar = Calendar.getInstance()
            startDateCalendar!!.timeInMillis = startDate!!
        }
        return startDateCalendar!!
    }

    private fun getEndDate(): Calendar? {
        if (endDateCalendar == null && endDate != null) {
            endDateCalendar = Calendar.getInstance()
            endDateCalendar!!.timeInMillis = endDate!!
        }
        return endDateCalendar
    }
}