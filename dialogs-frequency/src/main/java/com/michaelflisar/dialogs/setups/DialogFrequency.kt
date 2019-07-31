package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.enums.FrequencyUnit
import com.michaelflisar.dialogs.enums.RepeatType
import com.michaelflisar.dialogs.fragments.DialogFrequencyFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogFrequency(
    // base setup
    override val id: Int,
    override val title: Text? = null,
    override val posButton: Text = Text.TextRes(android.R.string.ok),
    override val negButton: Text? = null,
    override val neutrButton: Text? = null,
    override val cancelable: Boolean = true,
    override val extra: Bundle? = null,
    override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,

    // special setup
    val frequency: FrequencySetup = FrequencySetup(FrequencyUnit.Day),
    val validFrequencyUnits: List<FrequencyUnit> = FrequencyUnit.values().toList(),
    val validRepeatTypes: List<RepeatType> = RepeatType.values().toList(),
    val askForStart: Boolean = true,
    val askForEnd: Boolean = true,
    val dialogStartDateId: Int = Integer.MAX_VALUE,
    val dialogEndDateId: Int = Integer.MAX_VALUE - 1,
    val dialogEveryXUnitId: Int = Integer.MAX_VALUE - 2,
    val dialogNTimesFactorId: Int = Integer.MAX_VALUE - 3,
    val dialogMonthDayId: Int = Integer.MAX_VALUE - 4

) : BaseDialogSetup {

    override fun create() = DialogFrequencyFragment.create(this)
}