package com.michaelflisar.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.list.databinding.MdfDefaultListItemBinding

class ListItemAdapter(
    private val iconSize: Int,
    private val mode: DialogList.SelectionMode,
    private var items: List<DialogList.ListItem>,
    private val selectedIndices: MutableSet<Int>,
    private val onClickListener: (index: Int, item: DialogList.ListItem) -> Unit
) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            MdfDefaultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<DialogList.ListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setItemChecked(index: Int, checked: Boolean) {
        if (checked)
            selectedIndices.add(index)
        else
            selectedIndices.remove(index)
        notifyItemChanged(index)
    }

    fun getCheckedIndizes(): List<Int> = selectedIndices.toList()

    fun getCheckedItems(): List<DialogList.ListItem> {
        return selectedIndices
            .toList()
            .sorted()
            .map {
                items[it]
            }
    }

    fun toggleItemChecked(index: Int) {
        val isChecked = selectedIndices.contains(index)
        setItemChecked(index, !isChecked)
    }

    class ViewHolder(
        val adapter: ListItemAdapter,
        val binding: MdfDefaultListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.mdfIconLeft.layoutParams.apply {
                this.height = adapter.iconSize
                this.width = adapter.iconSize
            }

            val icon = when (adapter.mode) {
                DialogList.SelectionMode.SingleSelect -> MaterialDialogFragmentUtil.getThemeReference(
                    binding.root.context,
                    android.R.attr.listChoiceIndicatorSingle
                )
                DialogList.SelectionMode.MultiSelect -> MaterialDialogFragmentUtil.getThemeReference(
                    binding.root.context,
                    android.R.attr.listChoiceIndicatorMultiple
                )
                DialogList.SelectionMode.SingleClick -> null
                DialogList.SelectionMode.MultiClick -> null
            }

            if (icon == null)
                binding.mdfCheckboxRight.visibility = View.GONE
            else
                binding.mdfCheckboxRight.setButtonDrawable(icon)
        }

        fun bind(item: DialogList.ListItem) {
            if (item.icon == null) {
                binding.mdfIconLeft.visibility = View.GONE
            } else {
                binding.mdfIconLeft.visibility = View.GONE
                item.icon?.invoke(binding.mdfIconLeft)
            }
            item.text.display(binding.mdfText)
            binding.root.setOnClickListener {
                adapter.onClickListener(bindingAdapterPosition, item)
            }
            binding.mdfCheckboxRight.isChecked =
                adapter.selectedIndices.contains(bindingAdapterPosition)
        }
    }
}