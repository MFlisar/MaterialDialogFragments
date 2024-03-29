package com.michaelflisar.dialogs.classes

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.list.databinding.MdfDefaultListItemBinding

class ListItemAdapter(
    private val context: Context,
    private val setup: Setup,
    private var filter: String,
    private val selectedIds: MutableSet<Int>,
    private val onClickListener: (index: Int, item: DialogList.ListItem) -> Unit
) : ListAdapter<ListItemAdapter.ItemWrapper, ListItemAdapter.ViewHolder>(DiffCallback) {

    private var unfilteredItems: List<DialogList.ListItem> = emptyList()
    private var filteredItems: List<DialogList.ListItem> = emptyList()

    companion object {
        object PAYLOAD_FILTER
        object DiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
            override fun areItemsTheSame(
                oldItem: ItemWrapper,
                newItem: ItemWrapper
            ): Boolean {
                return oldItem.item.id == newItem.item.id
            }

            override fun areContentsTheSame(
                oldItem: ItemWrapper,
                newItem: ItemWrapper
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: ItemWrapper, newItem: ItemWrapper): Any? {
                if (oldItem.item == newItem.item && oldItem.filter != newItem.filter)
                    return PAYLOAD_FILTER
                return null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            MdfDefaultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position).item, emptyList())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        holder.bind(getItem(position).item, payload)
    }

    fun updateItems(items: List<DialogList.ListItem>) {
        unfilteredItems = items
        submitFilteredList()
    }

    fun updateFilter(filter: String) {
        this.filter = filter
        submitFilteredList()
    }

    private fun submitFilteredList() {
        // 1) calculate filtered items
        filteredItems = if (setup.filter == null) {
            unfilteredItems
        } else {
            unfilteredItems.filter { setup.filter.matches(context, it, filter) }
        }
        // 2) remove all selected not visible items... its not obvious to the user that those are selected if they are not visible!
        // TODO: events could only emit "visible selection" alternatively...
        if (setup.filter?.unselectInvisibleItems == true) {
            val invalidSelectedIds = unfilteredItems
                .map { it.id }
                .toMutableSet()
                .let {
                    it.removeAll(filteredItems.map { it.id }.toSet())
                    it
                }
            selectedIds.removeAll(invalidSelectedIds)
        }
        // 3) update adapter
        submitList(filteredItems.map { ItemWrapper(it, filter) })
    }

    fun setItemChecked(item: DialogList.ListItem, checked: Boolean) {
        if (checked)
            selectedIds.add(item.id)
        else
            selectedIds.remove(item.id)
        val index = filteredItems.indexOfFirst { it.id == item.id }
        Log.d("CHECKED", "${item.text.get(context)} -> $checked -> index = $index")
        notifyItemChanged(index)
    }

    fun getCheckedIds(): Set<Int> = selectedIds

    fun getCheckedItemsForResult(): List<DialogList.ListItem> {
        val items = if (setup.filter?.unselectInvisibleItems == false) {
            // in this case there may be more items selected than visible => we base the result on the visible items only here!
            filteredItems
        } else unfilteredItems

        return selectedIds
            .map { id -> items.find { it.id == id }!! }
            .toList()
    }

    fun toggleItemChecked(item: DialogList.ListItem) {
        val isChecked = selectedIds.contains(item.id)
        setItemChecked(item, !isChecked)
    }

    class ViewHolder(
        val adapter: ListItemAdapter,
        val binding: MdfDefaultListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.mdfIconLeft.layoutParams.apply {
                this.height = adapter.setup.iconSize
                this.width = adapter.setup.iconSize
            }

            val icon = when (adapter.setup.mode) {
                DialogList.SelectionMode.SingleSelect -> MaterialDialogFragmentUtil.getThemeReference(
                    binding.root.context,
                    R.attr.listChoiceIndicatorSingle
                )
                DialogList.SelectionMode.MultiSelect -> MaterialDialogFragmentUtil.getThemeReference(
                    binding.root.context,
                    R.attr.listChoiceIndicatorMultiple
                )
                DialogList.SelectionMode.SingleClick -> null
                DialogList.SelectionMode.MultiClick -> null
            }

            if (icon == null)
                binding.mdfCheckboxRight.visibility = View.GONE
            else
                binding.mdfCheckboxRight.setButtonDrawable(icon)
        }

        fun bind(item: DialogList.ListItem, payload: List<Any>) {

            val onlyUpdateTexts = payload.isNotEmpty() && payload.contains(PAYLOAD_FILTER)

            if (!onlyUpdateTexts) {
                // 1) icon
                val icon = item.displayIcon(binding.mdfIconLeft)
                binding.mdfIconLeft.visibility = if (icon) View.VISIBLE else View.GONE
                // 2) checked state
                binding.mdfCheckboxRight.isChecked =
                    adapter.selectedIds.contains(item.id)
                // 3) click listener
                binding.root.setOnClickListener {
                    adapter.onClickListener(bindingAdapterPosition, item)
                }
            }

            // 4) main text
            if (adapter.setup.filter == null)
                item.text.display(binding.mdfText)
            else
                adapter.setup.filter.displayText(binding.mdfText, item, adapter.filter)

            // 5) optional sub text
            val subText = if (adapter.setup.filter == null)
                item.subText.display(binding.mdfSubText)
            else
                adapter.setup.filter.displaySubText(binding.mdfSubText, item, adapter.filter)
            binding.mdfSubText.visibility = if (subText.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    class Setup(
        val iconSize: Int,
        val mode: DialogList.SelectionMode,
        val filter: DialogList.Filter?
    ) {
        constructor(dialog: DialogList) : this(
            dialog.itemsProvider.iconSize,
            dialog.selectionMode,
            dialog.filter
        )
    }

    data class ItemWrapper(
        val item: DialogList.ListItem,
        val filter: String
    )
}