package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.adapters.DayOfMonthItem
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.classes.Frequency
import com.michaelflisar.dialogs.classes.FrequencySetup
import com.michaelflisar.dialogs.classes.MonthDay
import com.michaelflisar.dialogs.classes.SendResultType
import com.michaelflisar.dialogs.enums.*
import com.michaelflisar.dialogs.events.MaterialDialogEvent
import com.michaelflisar.dialogs.extension.clearTime
import com.michaelflisar.dialogs.extension.setCheckedWithoutListener
import com.michaelflisar.dialogs.extension.setOnOffLabels
import com.michaelflisar.dialogs.frequency.R
import com.michaelflisar.dialogs.frequency.databinding.DialogFrequencyBinding
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback
import com.michaelflisar.dialogs.setups.*
import com.michaelflisar.dialogs.setups.DialogMonthDay
import com.michaelflisar.text.asText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

class DialogFrequencyFragment : MaterialDialogFragment<DialogFrequency>(),
    AdapterView.OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener, DialogFragmentCallback {

    companion object {

        val IGNORE_FLAG = DialogFrequencyFragment::class.java.simpleName

        fun create(setup: DialogFrequency): DialogFrequencyFragment {
            val dlg = DialogFrequencyFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var lastFrequency: FrequencySetup
    private lateinit var binding: DialogFrequencyBinding

    private val itemAdapter = ItemAdapter<DayOfMonthItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastFrequency = savedInstanceState.getParcelable("lastFrequency")!!
        } else {
            lastFrequency = setup.frequency
        }
    }

    override fun onDialogResultAvailable(event: MaterialDialogEvent): Boolean {
        when (event.id) {
            setup.dialogStartDateId -> {
                if (event is DialogDateTime.Event) {
                    (event as? DialogDateTime.Event.Data)?.let {
                        lastFrequency.startDate = it.date.clearTime()
                        updateView(false)
                    }
                    return true
                }
            }
            setup.dialogEndDateId -> {
                if (event is DialogDateTime.Event) {
                    (event as? DialogDateTime.Event.Data)?.let {
                        lastFrequency.endDate = it.date.clearTime()
                        updateView(false)
                    }
                    return true
                } else if (event is DialogNumber.Event) {
                    (event as? DialogNumber.Event.Data)?.let {
                        lastFrequency.endTimes = it.value
                        updateView(false)
                    }
                    return true
                }
            }
            setup.dialogEveryXUnitId -> {
                if (event is DialogNumber.Event) {
                    (event as? DialogNumber.Event.Data)?.let {
                        lastFrequency.everyXUnit = it.value
                        updateView(false)
                    }
                    return true
                }
            }
            setup.dialogNTimesFactorId -> {
                if (event is DialogNumber.Event) {
                    (event as? DialogNumber.Event.Data)?.let {
                        lastFrequency.nTimesFactor = it.value
                        updateView(false)
                    }
                    return true
                }
            }
            setup.dialogMonthDayId -> {
                if (event is DialogMonthDay.Event) {
                    (event as? DialogMonthDay.Event.Data)?.let {
                        itemAdapter.add(DayOfMonthItem(it.day))
                        lastFrequency.monthDays.add(it.day)
                        binding.rvMonthDays.scrollToPosition(itemAdapter.adapterItemCount - 1)
                        updateView(false)
                    }
                    return true
                }
            }
        }
        return false
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        val hasToolbar = true

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(requireActivity(), this, !hasToolbar)

        dialog.customView(
            R.layout.dialog_frequency,
            scrollable = false,
            noVerticalPadding = hasToolbar
        )
            .positiveButton(setup) {
                val result = lastFrequency.calculateFrequency(requireActivity())
                when (result) {
                    is FrequencySetup.FrequencyResult.Error -> {
                        DialogInfo(
                            -1,
                            R.string.mdf_error_title.asText(),
                            result.error.asText()
                        )
                            .show(requireActivity())
                    }
                    is FrequencySetup.FrequencyResult.Success -> {
                        sendEvent(
                            DialogFrequency.Event.Data(
                                setup,
                                MaterialDialogButton.Positive,
                                result.frequency
                            )
                        )
                        dismiss()
                    }
                }
            }
        dialog
            .noAutoDismiss()

        dialog
            .negativeButton(setup) {
                sendEvent(DialogFrequency.Event.Empty(setup, MaterialDialogButton.Negative))
                dismiss()
            }
            .neutralButton(setup) {
                sendEvent(DialogFrequency.Event.Empty(setup, MaterialDialogButton.Neutral))
            }

        binding = DialogFrequencyBinding.bind(dialog.getCustomView())

        if (hasToolbar) {
            binding.toolbar.title = setup.title?.get(requireActivity())
        }

        updateView(true)
        return dialog
    }

    fun updateView(init: Boolean) {

        // 1) Update type spinner
        UIUtil.setAdapter(
            this,
            init,
            binding.spFrequencyType,
            setup.validFrequencyUnits.map { requireActivity().getString(it.labelTypeRes) }
                .toMutableList(),
            lastFrequency.unit.ordinal,
            setup.title != null
        )

        // no title => we move the toolbar spinner to the left and make it fill it's parent
        if (init && setup.title == null) {
            (binding.spFrequencyType.layoutParams as Toolbar.LayoutParams).gravity = Gravity.LEFT
            (binding.spFrequencyType.layoutParams as Toolbar.LayoutParams).width =
                Toolbar.LayoutParams.MATCH_PARENT
        }

        // 2) Update repeat type spinner
        val enableRepeatType = lastFrequency.unit.supportIrregularRepeat
        UIUtil.setAdapter(
            this,
            init,
            binding.spRepeatType,
            setup.validRepeatTypes.map { requireActivity().getString(it.typeRes) }.toMutableList(),
            lastFrequency.repeatType.ordinal,
            false
        )
        binding.spRepeatType.isEnabled = enableRepeatType

        // 3) Update every x unit views (regular repeat type)
        val showEveryXTimes = lastFrequency.repeatType == RepeatType.Regular
        binding.tvBeforeEveryXTimes.setText(lastFrequency.unit.labelBeforeEveryXTime)
        binding.etEveryXTimes.setText(lastFrequency.everyXUnit.toString())
        binding.tvAfterEveryXTimes.setText(lastFrequency.unit.labelAfterEveryXTime)
        binding.llEveryXTimes.visibility = if (showEveryXTimes) View.VISIBLE else View.GONE
        UIUtil.setEditText(
            this,
            init,
            binding.etEveryXTimes,
            lastFrequency.everyXUnit.toString(),
            null,
            setup.dialogEveryXUnitId,
            getString(R.string.mdf_dialog_title_select_number),
            null
        )

        // 4) Update n times views (irregular repeat type)
        val showNTimes = lastFrequency.repeatType == RepeatType.Irregular
        if (showNTimes) {
            binding.tvBeforeNTimes.setText(lastFrequency.unit.labelBeforeNTimes)
            binding.etNTimes.setText(lastFrequency.nTimesFactor.toString())
            binding.tvAfterNTimes.setText(lastFrequency.unit.labelAfterNTimes)
        }
        binding.llNTimes.visibility = if (showNTimes) View.VISIBLE else View.GONE
        val max = when (lastFrequency.unit) {
            FrequencyUnit.Day -> null
            FrequencyUnit.Week -> 7
            FrequencyUnit.Month -> 31
            FrequencyUnit.Year -> null
        }
        UIUtil.setEditText(
            this,
            init,
            binding.etNTimes,
            lastFrequency.nTimesFactor.toString(),
            null,
            setup.dialogNTimesFactorId,
            getString(R.string.mdf_dialog_title_select_number),
            max
        )

        // 5) Update week day views
        val showWeekDays =
            lastFrequency.unit == FrequencyUnit.Week && lastFrequency.repeatType == RepeatType.Regular
        binding.llWeekDays1.visibility = if (showWeekDays) View.VISIBLE else View.GONE
        binding.llWeekDays2.visibility = if (showWeekDays) View.VISIBLE else View.GONE
        val weekDays = WeekDay.sorted()
        val btWeekDays = listOf(
            binding.btWeekDay1,
            binding.btWeekDay2,
            binding.btWeekDay3,
            binding.btWeekDay4,
            binding.btWeekDay5,
            binding.btWeekDay6,
            binding.btWeekDay7
        )
        if (init) {
            for (i in 0 until 7) {
                btWeekDays[i].setOnOffLabels(weekDays[i].calendarDay)
            }
        }
        for (i in 0 until 7) {
            btWeekDays[i].setCheckedWithoutListener(
                lastFrequency.weekDays.contains(weekDays[i]),
                this
            )
        }

        // 6) Update month day views
        val showMonthDays =
            lastFrequency.unit == FrequencyUnit.Month && lastFrequency.repeatType == RepeatType.Regular
        binding.llMonthDays.visibility = if (showMonthDays) View.VISIBLE else View.GONE
        if (init) {
            binding.rvMonthDays.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)

            fastAdapter.addEventHook(object : ClickEventHook<DayOfMonthItem>() {

                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                    return (viewHolder as DayOfMonthItem.ViewHolder).binding.ivDelete
                }

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<DayOfMonthItem>,
                    item: DayOfMonthItem
                ) {
                    itemAdapter.remove(position)
                    lastFrequency.monthDays.remove(item.day)
                    updateView(false)
                }
            })
            binding.rvMonthDays.adapter = fastAdapter

            binding.btAddDay.setOnClickListener {
                // TODO: add not allowed month days
                DialogMonthDay(
                    setup.dialogMonthDayId,
                    R.string.mdf_dialog_title_add_day.asText(),
                    monthDay = MonthDay.DayOfMonth(1)
                )
                    .show(this, customSendResultType = SendResultType.ParentFragment)
            }
        }

        // 7) start
        val showStartDate = setup.askForStart
        binding.llStart.visibility = if (showStartDate) View.VISIBLE else View.GONE
        if (showStartDate) {
            UIUtil.setAdapter(
                this,
                init,
                binding.spStart,
                StartType.values().map { requireActivity().getString(it.typeRes) }.toMutableList(),
                lastFrequency.startType.ordinal,
                false
            )
            UIUtil.setEditText(
                this,
                init,
                binding.etStart,
                Frequency.formatMillis(lastFrequency.startDate.timeInMillis),
                lastFrequency.startDate.timeInMillis,
                setup.dialogStartDateId,
                null,
                null
            )
            val showStartEditText = lastFrequency.startType == StartType.SelectDate
            binding.etStart.visibility = if (showStartEditText) View.VISIBLE else View.GONE
        }

        // 8) end
        val showEndType = setup.askForEnd
        binding.llEnd.visibility = if (showEndType) View.VISIBLE else View.GONE
        if (showEndType) {
            UIUtil.setAdapter(
                this,
                init,
                binding.spEnd,
                EndType.values().map { requireActivity().getString(it.typeRes) }.toMutableList(),
                lastFrequency.endType.ordinal,
                false
            )
            val endDate =
                if (lastFrequency.endType == EndType.UntilDate) lastFrequency.endDate else null
            val endTime =
                if (lastFrequency.endType == EndType.UntilTimes) lastFrequency.endTimes else null

            val showEndEditText = endDate != null || endTime != null

            val value = if (endDate != null) {
                Frequency.formatMillis(endDate.timeInMillis)
            } else if (endTime != null) {
                endTime.toString()
            } else null

            val datetime = if (endDate != null) {
                endDate.timeInMillis
            } else null

            UIUtil.setEditText(
                this,
                init,
                binding.etEnd,
                value ?: "",
                datetime,
                setup.dialogEndDateId,
                endTime?.let { getString(R.string.mdf_dialog_title_select_number) },
                null
            )

            binding.etEnd.visibility = if (showEndEditText) View.VISIBLE else View.GONE
        }

        // 9) Update info
        val frequency = lastFrequency.calculateFrequency(requireActivity())
        val info = when (frequency) {
            is FrequencySetup.FrequencyResult.Error -> frequency.error
            is FrequencySetup.FrequencyResult.Success -> frequency.frequency.toReadableString(
                requireActivity(),
                setup.askForStart,
                setup.askForEnd
            )
        }
        binding.tvInfo.text = info
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("lastFrequency", lastFrequency)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            R.id.spFrequencyType -> lastFrequency.unit = setup.validFrequencyUnits[position]
            R.id.spRepeatType -> lastFrequency.repeatType = setup.validRepeatTypes[position]
            R.id.spStart -> lastFrequency.startType = StartType.values()[position]
            R.id.spEnd -> lastFrequency.endType = EndType.values()[position]
            else -> throw RuntimeException("Type not handled!")
        }

        if (lastFrequency.unit == FrequencyUnit.Day && lastFrequency.repeatType == RepeatType.Irregular) {
            lastFrequency.repeatType = RepeatType.Regular
        }

        updateView(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        fun addOrRemove(isChecked: Boolean, day: WeekDay) {
            if (isChecked) {
                lastFrequency.weekDays.add(day)
            } else {
                lastFrequency.weekDays.remove(day)
            }
        }

        val weekDays = WeekDay.sorted()
        when (buttonView?.id) {
            R.id.btWeekDay1 -> addOrRemove(isChecked, weekDays[0])
            R.id.btWeekDay2 -> addOrRemove(isChecked, weekDays[1])
            R.id.btWeekDay3 -> addOrRemove(isChecked, weekDays[2])
            R.id.btWeekDay4 -> addOrRemove(isChecked, weekDays[3])
            R.id.btWeekDay5 -> addOrRemove(isChecked, weekDays[4])
            R.id.btWeekDay6 -> addOrRemove(isChecked, weekDays[5])
            R.id.btWeekDay7 -> addOrRemove(isChecked, weekDays[6])
            else -> throw RuntimeException("Type not handled!")
        }

        updateView(false)
    }
}
