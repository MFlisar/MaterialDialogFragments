package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class Frequency : Parcelable {

    abstract var start: Long
    abstract var end: EndData

    @Parcelize
    class Daily(
        var everyXDays: Int = 1,
        var numberOfTimesPerDay: Int = 1,
        override var start: Long,
        override var end: EndData
    ) : Frequency()

    @Parcelize
    class Weekly(
        var everyXWeeks: Int = 1,
        var numberOfTimesPerDay: Int = 1,
        /*
         * zero based week day index, starting at 0 for monday until 6 for sunday
         */
        var days: List<Int>,
        override var start: Long,
        override var end: EndData
    ) : Frequency()

    @Parcelize
    class NTimesWeekly(
        var everyXWeeks: Int = 1,
        var numberOfTimesPerDay: Int = 1,
        var nTimesFactor: Int = 1,
        override var start: Long,
        override var end: EndData
    ) : Frequency()

    @Parcelize
    class Monthly(
        var everyXMonths: Int = 1,
        var numberOfTimesPerDay: Int = 1,
        /*
        * zero based day of month index
        */
        var days: List<Int>,
        override var start: Long,
        override var end: EndData
    ) : Frequency()

    @Parcelize
    class NTimesMonthly(
        var everyXMonths: Int = 1,
        var numberOfTimesPerDay: Int = 1,
        var nTimesFactor: Int = 1,
        override var start: Long,
        override var end: EndData
    ) : Frequency()


    sealed class EndData : Parcelable {
        @Parcelize
        object Never : EndData()

        @Parcelize
        class Times(val times: Int) : EndData()

        @Parcelize
        class Date(val date: Long) : EndData()
    }
}