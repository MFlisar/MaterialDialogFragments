package com.michaelflisar.dialogs.fastAdapter

import android.view.View
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.classes.App
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.michaelflisar.dialogs.app.databinding.ItemSimpleAppBinding

class SimpleAppItem(
        val app: App
) : AbstractItem<SimpleAppItem.ViewHolder>() {

    override val type = R.id.fast_adapter_simple_app_item
    override val layoutRes = R.layout.item_simple_app

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SimpleAppItem>(view) {
        var binding: ItemSimpleAppBinding = ItemSimpleAppBinding.bind(view)

        override fun bindView(item: SimpleAppItem, payloads: List<Any>) {
            binding.tvAppName.text = item.app.name
            binding.tvPackageName.text = item.app.packageName
            binding.ivIcon.setImageDrawable(item.app.resolveInfo?.loadIcon(binding.root.context.packageManager))
        }

        override fun unbindView(item: SimpleAppItem) {
            binding.tvAppName.text = null
            binding.tvPackageName.text = null
            binding.ivIcon.setImageDrawable(null)
        }
    }
}