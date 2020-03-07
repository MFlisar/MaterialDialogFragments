package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.UIUtil
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.classes.MonthDay
import com.michaelflisar.dialogs.enums.MonthDayType
import com.michaelflisar.dialogs.enums.WeekDay
import com.michaelflisar.dialogs.events.DialogMonthDayEvent
import com.michaelflisar.dialogs.frequency.R
import com.michaelflisar.dialogs.frequency.databinding.DialogMonthDayBinding
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogMonthDay

internal class DialogMonthDayFragment : MaterialDialogFragment<DialogMonthDay>(), AdapterView.OnItemSelectedListener {

    companion object {

        fun create(setup: DialogMonthDay): DialogMonthDayFragment {
            val dlg = DialogMonthDayFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var binding: DialogMonthDayBinding

    private var monthDayType = MonthDayType.DayOfMonth
    private var dayNumber = 0
    private var dayNumberBeginning = true
    private var weekdayNumber = 0
    private var weekDay = WeekDay.firstDayOfWeek()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            monthDayType = savedInstanceState.getParcelable("monthDayType")!!
            dayNumber = savedInstanceState.getInt("dayNumber")
            dayNumberBeginning = savedInstanceState.getBoolean("dayNumberBeginning")
            weekdayNumber = savedInstanceState.getInt("weekdayNumber")
            weekDay = savedInstanceState.getParcelable("weekDay")!!
        }
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(activity!!, this)

        dialog.customView(
                R.layout.dialog_month_day,
                scrollable = false,
                horizontalPadding = true
        )
                .positiveButton(setup) {
                    sendEvent(
                            DialogMonthDayEvent(
                                    setup,
                                    WhichButton.POSITIVE.ordinal,
                                    DialogMonthDayEvent.Data(createMonthDay())
                            )
                    )
                    dismiss()
                }
                .noAutoDismiss()

        dialog
                .negativeButton(setup) {
                    sendEvent(DialogMonthDayEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                    dismiss()
                }

                .neutralButton(setup) {
                    sendEvent(DialogMonthDayEvent(setup, WhichButton.NEUTRAL.ordinal, null))
                }

        binding = DataBindingUtil.bind(dialog.getCustomView())!!

        updateView(true)
        return dialog
    }

    fun updateView(init: Boolean) {

        // 1) MonthDayType spinner
        UIUtil.setAdapter(
                this,
                init,
                binding.spMonthDayType,
                MonthDayType.values().map { activity!!.getString(it.typeRes) }.toMutableList(),
                monthDayType.ordinal,
                false
        )

        // 2) Day numbers spinner
        val dayNumbers = ArrayList<Int>()
        for (i in 0 until 31) {
            dayNumbers.add(i)
        }
        UIUtil.setAdapter(
                this,
                init,
                binding.spDayNumber,
                dayNumbers.map { activity!!.getString(R.string.mdf_day_n, it + 1) }.toMutableList(),
                dayNumber,
                false
        )
        binding.spDayNumber.visibility = if (monthDayType == MonthDayType.DayOfMonth) View.VISIBLE else View.GONE

        // 3) Day numbers beginning type spinner
        val dayNumberBeginningValues = ArrayList<Int>()
        dayNumberBeginningValues.add(R.string.mdf_day_from_start)
        dayNumberBeginningValues.add(R.string.mdf_day_from_end)
        UIUtil.setAdapter(
                this,
                init,
                binding.spDayNumberBeginning,
                dayNumberBeginningValues.map { activity!!.getString(it) }.toMutableList(),
                if (dayNumberBeginning) 0 else 1,
                false
        )
        binding.spDayNumberBeginning.visibility =
                if (monthDayType == MonthDayType.DayOfMonth) View.VISIBLE else View.GONE

        // 4) WeekDay spinner
        UIUtil.setAdapter(
                this,
                init,
                binding.spWeekday,
                WeekDay.sorted().map { it.longName() }.toMutableList(),
                WeekDay.sorted().indexOf(weekDay),
                false
        )
        binding.spWeekday.visibility = if (monthDayType == MonthDayType.DayOfWeek) View.VISIBLE else View.GONE

        // 5) WeekDay number spinner
        val weekDayNumbers = ArrayList<Int>()
        for (i in 0 until 5) {
            weekDayNumbers.add(i)
        }
        UIUtil.setAdapter(
                this,
                init,
                binding.spWeekdayNumber,
                weekDayNumbers.map { activity!!.getString(R.string.mdf_weekday_n, it + 1) }.toMutableList(),
                weekdayNumber,
                false
        )
        binding.spWeekdayNumber.visibility = if (monthDayType == MonthDayType.DayOfWeek) View.VISIBLE else View.GONE

        // 6) Info
        val monthDay = createMonthDay()
        binding.tvInfo.text = monthDay.toReadableString(activity!!, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("monthDayType", monthDayType)
        outState.putInt("dayNumber", dayNumber)
        outState.putBoolean("dayNumberBeginning", dayNumberBeginning)
        outState.putInt("weekdayNumber", weekdayNumber)
        outState.putParcelable("weekDay", weekDay)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            R.id.spMonthDayType -> monthDayType = MonthDayType.values()[position]
            R.id.spDayNumber -> dayNumber = position
            R.id.spDayNumberBeginning -> dayNumberBeginning = position == 0
            R.id.spWeekdayNumber -> weekdayNumber = position
            R.id.spWeekday -> weekDay = WeekDay.sorted()[position]
            else -> throw RuntimeException("Type not handled!")
        }

        updateView(false)
    }

    private fun createMonthDay(): MonthDay {
        return when (monthDayType) {
            MonthDayType.DayOfMonth -> MonthDay.DayOfMonth(dayNumber, dayNumberBeginning)
            MonthDayType.DayOfWeek -> MonthDay.DayOfWeek(weekDay, weekdayNumber)
        }
    }
}