package com.michaelflisar.dialogs

import java.text.DateFormat
import java.util.*

object DialogFrequencySetup {

    var FREQUENCY_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    var DEFAULT_END_DAY_OFFSET = 27 // 4 Weeks, from Mon to Sun
    var DEFAULT_END_TIMES = 10

    var FIRST_DAY_OF_WEEK_IS_MONDAY = true
}