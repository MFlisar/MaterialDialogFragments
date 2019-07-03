package com.michaelflisar.dialogs.extension

import android.widget.CompoundButton
import android.widget.ToggleButton
import com.michaelflisar.dialogs.CalendarUtil

internal fun ToggleButton.setCheckedWithoutListener(checked: Boolean, listener: CompoundButton.OnCheckedChangeListener?) {
    setOnCheckedChangeListener(null)
    isChecked = checked
    setOnCheckedChangeListener(listener)
}

internal fun ToggleButton.setOnOffLabels(day: Int) {
    textOff = CalendarUtil.WEEKDAYS[day]
    textOn = CalendarUtil.WEEKDAYS[day]
    requestLayout()
}