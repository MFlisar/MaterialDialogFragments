package com.michaelflisar.dialogs.fastAdapter

import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ItemSimpleAppBinding
import com.michaelflisar.dialogs.classes.App
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils

class SimpleAppItem(
        val app: App
) : AbstractItem<SimpleAppItem.ViewHolder>() {

    override val type = R.id.fast_adapter_simple_app_item
    override val layoutRes = R.layout.item_simple_app

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(val view: View) : FastAdapter.ViewHolder<SimpleAppItem>(view) {
        var binding: ItemSimpleAppBinding = ItemSimpleAppBinding.bind(view)

        private val accentColor by lazy {
            val typedValue = TypedValue()
            val a: TypedArray = view.context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
            val color = a.getColor(0, 0)
            a.recycle()
            color
        }

        override fun bindView(item: SimpleAppItem, payloads: List<Any>) {
            binding.tvAppName.text = item.app.name
            binding.tvPackageName.text = item.app.packageName
            binding.ivIcon.setImageDrawable(item.app.resolveInfo?.loadIcon(binding.root.context.packageManager))
            binding.root.background = FastAdapterUIUtils.getSelectableBackground(binding.root.context, accentColor, true)
        }

        override fun unbindView(item: SimpleAppItem) {
            binding.tvAppName.text = null
            binding.tvPackageName.text = null
            binding.ivIcon.setImageDrawable(null)
        }
    }
}