package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.events.DialogFrequencyEvent
import com.michaelflisar.dialogs.extension.setCheckedWithoutListener
import com.michaelflisar.dialogs.extension.setOnOffLabels
import com.michaelflisar.dialogs.extension.setSimpleTextWatcher
import com.michaelflisar.dialogs.frequency.R
import com.michaelflisar.dialogs.frequency.databinding.DialogFrequencyBinding
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogFrequency
import com.michaelflisar.dialogs.title
import java.util.*

class DialogFrequencyFragment : BaseDialogFragment<DialogFrequency>(), AdapterView.OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {

    companion object {

        fun create(setup: DialogFrequency): DialogFrequencyFragment {
            val dlg = DialogFrequencyFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var lastFrequency: FrequencySetup
    private lateinit var binding: DialogFrequencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastFrequency = savedInstanceState.getParcelable("lastFrequency")
        } else {
            lastFrequency = setup.frequency
        }
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        val hasToolbar = true

        val dialog = MaterialDialog(activity!!)
            //
            .customView(
                R.layout.dialog_frequency,
                scrollable = false,
                noVerticalPadding = hasToolbar
            )
            .positiveButton(setup.posButton) {
                if (isValid(true)) {
                    lastFrequency.finish()
                    sendEvent(DialogFrequencyEvent(setup, WhichButton.POSITIVE.ordinal, DialogFrequencyEvent.Data(lastFrequency)))
                    dismiss()
                }
            }
            .cancelable(setup.cancelable)
            .noAutoDismiss()
        this.isCancelable = setup.cancelable

        if (!hasToolbar) {
            dialog.title(setup.title)
        }

        setup.negButton?.let {
            dialog.negativeButton(it) {
                sendEvent(DialogFrequencyEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                dismiss()
            }
        }

        setup.neutrButton?.let {
            dialog.neutralButton(it) {
                sendEvent(DialogFrequencyEvent(setup, WhichButton.NEUTRAL.ordinal, null))
            }
        }

        binding = DataBindingUtil.bind(dialog.getCustomView())!!

        if (hasToolbar) {
            binding.toolbar.title = setup.title.get(activity!!)
        }

        updateView(true)
        return dialog
    }

    fun updateView(init: Boolean) {

        // 1) Update type spinner
        setAdapter(
            init,
            binding.spFrequencyType,
            setup.validFrequencyUnits.map { activity!!.getString(it.labelTypeRes) },
            lastFrequency.unit.ordinal
        )

        // 2) Update repeat type spinner
        val enableRepeatType = lastFrequency.unit != FrequencyUnit.Day
        setAdapter(
            init,
            binding.spRepeatType,
            setup.validRepeatTypes.map { activity!!.getString(it.typeRes) },
            lastFrequency.repeatType.ordinal
        )
        binding.spRepeatType.isEnabled = enableRepeatType

        // 3) Update every x unit views
        val showEveryXTimes = lastFrequency.repeatType == RepeatType.Regular
        binding.tvBeforeEveryXTimes.setText(lastFrequency.unit.labelBeforeEveryXTime)
        binding.etEveryXTimes.setText(lastFrequency.everyXUnit.toString())
        binding.tvAfterEveryXTimes.setText(lastFrequency.unit.labelAfterEveryXTime)
        binding.llEveryXTimes.visibility = if (showEveryXTimes) View.VISIBLE else View.GONE
        if (init) {
            binding.etEveryXTimes.setSimpleTextWatcher {
                lastFrequency.everyXUnit = it.toIntOrNull() ?: 1
            }
        }

        // 4) Update number of times
        binding.etNumberOfTimes.setText(lastFrequency.numberOfTimes.toString())
        if (init) {
            binding.etNumberOfTimes.setSimpleTextWatcher {
                lastFrequency.numberOfTimes = it.toIntOrNull() ?: 1
            }
        }

        // 5) Update week day views
        val showWeekDays = lastFrequency.unit == FrequencyUnit.Week && lastFrequency.repeatType == RepeatType.Regular
        binding.llWeekDays1.visibility = if (showWeekDays) View.VISIBLE else View.GONE
        binding.llWeekDays2.visibility = if (showWeekDays) View.VISIBLE else View.GONE
        if (init) {
            binding.btWeekDay1.setOnOffLabels(Calendar.MONDAY)
            binding.btWeekDay2.setOnOffLabels(Calendar.TUESDAY)
            binding.btWeekDay3.setOnOffLabels(Calendar.WEDNESDAY)
            binding.btWeekDay4.setOnOffLabels(Calendar.THURSDAY)
            binding.btWeekDay5.setOnOffLabels(Calendar.FRIDAY)
            binding.btWeekDay6.setOnOffLabels(Calendar.SATURDAY)
            binding.btWeekDay7.setOnOffLabels(Calendar.SUNDAY)
        }
        binding.btWeekDay1.setCheckedWithoutListener(lastFrequency.weedays.monday, this)
        binding.btWeekDay2.setCheckedWithoutListener(lastFrequency.weedays.tuesday, this)
        binding.btWeekDay3.setCheckedWithoutListener(lastFrequency.weedays.wednesday, this)
        binding.btWeekDay4.setCheckedWithoutListener(lastFrequency.weedays.thursday, this)
        binding.btWeekDay5.setCheckedWithoutListener(lastFrequency.weedays.friday, this)
        binding.btWeekDay6.setCheckedWithoutListener(lastFrequency.weedays.saturday, this)
        binding.btWeekDay7.setCheckedWithoutListener(lastFrequency.weedays.sunday, this)

        // 6) start
        val showStartDate = setup.askForStart
        binding.llStart.visibility = if (showStartDate) View.VISIBLE else View.GONE
        if (showStartDate) {
            setAdapter(
                init,
                binding.spStart,
                StartType.values().map { activity!!.getString(it.typeRes) },
                lastFrequency.startType.ordinal
            )
        }

        // 7) end
        val showEndType = setup.askForEnd
        binding.llEnd.visibility = if (showEndType) View.VISIBLE else View.GONE
        if (showEndType) {
            setAdapter(
                init,
                binding.spEnd,
                EndType.values().map { activity!!.getString(it.typeRes) },
                lastFrequency.endType.ordinal
            )
        }
    }

    fun isValid(showErrorDialog: Boolean): Boolean {

        // TODO

        return true
    }

    private fun setAdapter(init: Boolean, spinner: Spinner, items: List<String>, selectedIndex: Int) {
        if (init) {
            val adapter = NoPaddingArrayAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(selectedIndex, false)
            spinner.onItemSelectedListener = this
        } else {
            if (spinner.selectedItemPosition != selectedIndex) {
                spinner.setSelection(selectedIndex, false)
            }
        }
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
            R.id.spStart -> lastFrequency.startType= StartType.values()[position]
            R.id.spEnd -> lastFrequency.endType = EndType.values()[position]
            else -> throw RuntimeException("Type not handled!")
        }

        if (lastFrequency.unit == FrequencyUnit.Day && lastFrequency.repeatType == RepeatType.Irregular) {
            lastFrequency.repeatType = RepeatType.Regular
        }

        updateView(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.btWeekDay1 -> lastFrequency.weedays.monday = isChecked
            R.id.btWeekDay2 -> lastFrequency.weedays.tuesday = isChecked
            R.id.btWeekDay3 -> lastFrequency.weedays.wednesday = isChecked
            R.id.btWeekDay4 -> lastFrequency.weedays.thursday = isChecked
            R.id.btWeekDay5 -> lastFrequency.weedays.friday = isChecked
            R.id.btWeekDay6 -> lastFrequency.weedays.saturday = isChecked
            R.id.btWeekDay7 -> lastFrequency.weedays.sunday = isChecked
            else -> throw RuntimeException("Type not handled!")
        }

        updateView(false)
    }
}
