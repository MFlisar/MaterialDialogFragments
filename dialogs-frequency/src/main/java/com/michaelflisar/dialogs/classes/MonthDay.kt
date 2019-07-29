package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.CalendarUtil
import com.michaelflisar.dialogs.enums.WeekDay
import com.michaelflisar.dialogs.frequency.R
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.lang.RuntimeException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


sealed class MonthDay: Parcelable {

    abstract fun calcDate(year: Int, month: Int): Calendar

    abstract fun toReadableString(context: Context, forList: Boolean): String

    abstract fun toJSON(): JSONObject

    @Parcelize
    class DayOfMonth(var day: Int, var fromBeginning: Boolean = true): MonthDay() {

        override fun calcDate(year: Int, month: Int) = CalendarUtil.monthDayOfYear(year, month, day, fromBeginning)

        override fun toReadableString(context: Context, forList: Boolean): String {
            return if (fromBeginning) {
                context.getString(R.string.mdf_day_of_month_nth, day + 1)
            } else {
                context.getString(R.string.mdf_day_of_month_nth_last, day + 1)
            }
        }

        override fun toJSON() = MonthDay.toJSON(this)
    }

    @Parcelize
    class DayOfWeek(val weekDay: WeekDay, val number: Int): MonthDay() {

        override fun calcDate(year: Int, month: Int) = CalendarUtil.weekDayOfMonth(year, month, weekDay, number)

        override fun toReadableString(context: Context, forList: Boolean): String {
            return context.getString(R.string.mdf_weekday_of_month_info, number + 1, weekDay.longName())
        }

        override fun toJSON() = MonthDay.toJSON(this)
    }

    companion object {
        val SORTED = Comparator { o1: MonthDay, o2: MonthDay ->
            if (o1 is DayOfMonth && o2 is DayOfMonth) {
                o1.day.compareTo(o2.day)
            } else {
                if (o1 is DayOfMonth) {
                    -1
                } else if (o2 is DayOfMonth) {
                    1
                } else {
                    var c = (o1 as DayOfWeek).number.compareTo((o2 as DayOfWeek).number)
                    if (c == 0)
                        c = o1.weekDay.userSelectedOrderBase().compareTo(o2.weekDay.userSelectedOrderBase())
                    c
                }
            }
        }

        fun getAllSortedDayOfMonths(): ArrayList<DayOfMonth> {
            val items = ArrayList<DayOfMonth>()
            for (i in 0 until 31) {
                items.add(DayOfMonth(i))
            }
            return items
        }

        fun toJSON(day: MonthDay): JSONObject {
            val json = JSONObject()
            json.put("class", day::class.java.name)
            when (day) {
                is DayOfMonth -> {
                    json.put("day", day.day)
                    json.put("fromBeginning", day.fromBeginning)
                }
                is DayOfWeek -> {
                    json.put("weekDay", day.weekDay.ordinal)
                    json.put("number", day.number)
                }
            }

            return json
        }

        fun fromJSON(json: JSONObject):  MonthDay {
            val cls = json.getString("class")
            return when (cls) {
                DayOfMonth::class.java.name -> {
                    DayOfMonth(
                        json.getInt("day"),
                        json.getBoolean("fromBeginning")
                    )
                }
                DayOfWeek::class.java.name -> {
                    DayOfWeek(
                        WeekDay.values()[json.getInt("weekDay")],
                        json.getInt("number")
                    )
                }
                else -> throw RuntimeException("Invalid json for MonthDay!")
            }
        }
    }
}