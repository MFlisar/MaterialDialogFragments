package com.michaelflisar.dialogs.adapters

import android.view.View
import androidx.databinding.DataBindingUtil
import com.michaelflisar.dialogs.classes.MonthDay
import com.michaelflisar.dialogs.frequency.R
import com.michaelflisar.dialogs.frequency.databinding.ItemDayOfMonthBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class DayOfMonthItem(
    val day: MonthDay
) : AbstractItem<DayOfMonthItem.ViewHolder>() {

    override val type = R.id.fast_adapter_item_day_of_month
    override val layoutRes = R.layout.item_day_of_month

    override fun getViewHolder(v: View) = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<DayOfMonthItem>(view) {
        val binding: ItemDayOfMonthBinding = DataBindingUtil.bind(view)!!

        override fun bindView(item: DayOfMonthItem, payloads: MutableList<Any>) {
            binding.tvText.text = item.day.toReadableString(binding.root.context, false)
        }

        override fun unbindView(item: DayOfMonthItem) {
            binding.tvText.text = null
        }
    }
}