package com.michaelflisar.dialogs

import android.view.View
import androidx.databinding.DataBindingUtil
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.HeaderItemBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem


class HeaderItem(
        val title: String
) : AbstractItem<HeaderItem, HeaderItem.ViewHolder>() {

    override fun getType() = R.id.fast_adapter_header_item
    override fun getLayoutRes() = R.layout.header_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<HeaderItem>(view) {

        private val binding: HeaderItemBinding

        init {
            binding = DataBindingUtil.bind(view)!!
        }

        override fun bindView(item: HeaderItem, payloads: List<Any>) {
            binding.title.text = item.title
        }

        override fun unbindView(item: HeaderItem) {
            binding.title.text = null
        }
    }
}