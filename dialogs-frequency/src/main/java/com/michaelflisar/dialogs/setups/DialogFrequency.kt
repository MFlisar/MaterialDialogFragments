package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.enums.FrequencyUnit
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.enums.RepeatType
import com.michaelflisar.dialogs.events.MaterialDialogEvent
import com.michaelflisar.dialogs.fragments.DialogFrequencyFragment
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogFrequency(
    // base setup
    override val id: Int,
    override val title: Text? = null,
    override val posButton: Text = Text.Resource(android.R.string.ok),
    override val negButton: Text? = null,
    override val neutrButton: Text? = null,
    override val cancelable: Boolean = true,
    override val extra: Bundle? = null,
    override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
    override val style: DialogStyle = DialogStyle.Dialog,

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

    sealed class Event {
        class Empty(setup: BaseDialogSetup, button: MaterialDialogButton?) : Event(),
            MaterialDialogEvent by MaterialDialogEventImpl(setup, button)

        class Data(setup: BaseDialogSetup, button: MaterialDialogButton?, val frequency: Frequency<*>) :
            Event(), MaterialDialogEvent by MaterialDialogEventImpl(setup, button)
    }
}