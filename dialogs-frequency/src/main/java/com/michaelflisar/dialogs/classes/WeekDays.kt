package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
class WeekDays(
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false
) : Parcelable {

    fun asCalendarWeekDays(): List<Int> {
        val list = ArrayList<Int>()
        if (monday) {
            list.add(Calendar.MONDAY)
        }
        if (tuesday) {
            list.add(Calendar.TUESDAY)
        }
        if (wednesday) {
            list.add(Calendar.WEDNESDAY)
        }
        if (thursday) {
            list.add(Calendar.THURSDAY)
        }
        if (friday) {
            list.add(Calendar.FRIDAY)
        }
        if (saturday) {
            list.add(Calendar.SATURDAY)
        }
        if (sunday) {
            list.add(Calendar.SUNDAY)
        }
        return list
    }
}