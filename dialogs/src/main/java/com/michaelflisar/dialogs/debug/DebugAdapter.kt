package com.michaelflisar.dialogs.debug

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.core.databinding.RowAdapterDebugBinding


class DebugAdapter(val items: List<DebugDialog.Entry<*>>, val context: Context, val dialog: MaterialDialog, val darkTheme: Boolean, val withNumbering: Boolean) : RecyclerView.Adapter<DebugAdapter.ViewHolder>() {

    private var level = 0
    private var selectedIndices = arrayListOf<Int>()

    private val multiCheckMark by lazy {
        val typedValueAttr = TypedValue()
        context.theme.resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, typedValueAttr, true)
        typedValueAttr.resourceId
    }

    override fun getItemCount(): Int {
        return getItems(selectedIndices).size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_adapter_debug, parent, false), this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(selectedIndices, position)

        val text = when (item) {
            is DebugDialog.Entry.List -> item.name + " [" + item.getEntryByValue(DebugDialog.getInt(item)).name + "]"
            else -> item.name
        }
        val number = if (withNumbering) getNumber(position) else ""

        holder.binding.text.text = when (item) {
            is DebugDialog.Entry.Checkbox -> text
            else -> "$number$text"
        }
        val checked = when (item) {
            is DebugDialog.Entry.Checkbox -> DebugDialog.getBool(item)
            is DebugDialog.Entry.ListEntry -> DebugDialog.getInt(item.parent) == item.value
            else -> false
        }

        if (item is DebugDialog.Entry.Group || item is DebugDialog.Entry.List) {
            holder.binding.text.setCheckMarkDrawable(if (darkTheme) R.drawable.ic_arrow_forward_white_24dp  else R.drawable.ic_arrow_forward_black_24dp)
        } else if (item is DebugDialog.Entry.Checkbox) {
            holder.binding.text.setCheckMarkDrawable(multiCheckMark)
        } else {
            if (checked) {
                holder.binding.text.setCheckMarkDrawable(if (darkTheme) R.drawable.ic_check_white_24dp else R.drawable.ic_check_black_24dp)
            } else {
                holder.binding.text.checkMarkDrawable = null
            }
        }

        holder.binding.text.isChecked = checked
    }

    private fun getItems(selectedTopIndices: List<Int>): List<DebugDialog.Entry<*>> {
        var entries: List<DebugDialog.Entry<*>> = items
        for (i in selectedTopIndices) {
            entries = (entries[i] as DebugDialog.SubEntryHolder<*, *>).subEntries
        }
        return entries
    }

    private fun getItem(selectedTopIndices: List<Int>, index: Int): DebugDialog.Entry<*> {
        var entries = getItems(selectedTopIndices)
        return entries[index]
    }

    private fun getParentItem(selectedTopIndices: List<Int>): DebugDialog.Entry<*>? {
        if (selectedTopIndices.size == 0) {
            return null
        }
        var entries: List<DebugDialog.Entry<*>> = items
        for (i in 0 until selectedTopIndices.size - 1) {
            entries = (entries[selectedTopIndices[i]] as DebugDialog.SubEntryHolder<*, *>).subEntries
        }

        return entries[selectedTopIndices[selectedTopIndices.size - 1]]
    }

    internal fun goLevelUp(): Boolean {
        if (level > 0) {
            level--
            selectedIndices.removeAt(level)
            notifyDataSetChanged()
            DebugDialog.updateSubTitle(dialog, getParentItem(selectedIndices), if (withNumbering) getNumber(-1) else null)
            return true
        } else {
            return false
        }
    }

    private fun goLevelDown(index: Int) {
        level++
        selectedIndices.add(index)
        notifyDataSetChanged()
        DebugDialog.updateSubTitle(dialog, getParentItem(selectedIndices), if (withNumbering) getNumber(-1) else null)
    }

    private fun getNumber(pos: Int): String {
        var n = selectedIndices.joinToString(separator = ".") { "${it + 1}" }
        if (pos != -1)
            n += (if (selectedIndices.size > 0) "." else "") + "${pos + 1} "
        return n
    }

    // --------------------
    // ViewHolder
    // --------------------

    class ViewHolder(view: View, val adapter: DebugAdapter) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val binding: RowAdapterDebugBinding

        init {
            binding = DataBindingUtil.bind(view)!!
//            binding.label.setCheckMarkDrawable(if (adapter.darkTheme) R.drawable.ic_check_white_24dp else R.drawable.ic_check_black_24dp)
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = adapter.getItem(adapter.selectedIndices, adapterPosition)

            if (item is DebugDialog.SubEntryHolder<*, *>) {
                adapter.goLevelDown(adapterPosition)
            } else {
                val clickResult = item.onClick()
                if (clickResult.contains(DebugDialog.ClickResult.Notify)) {
                    adapter.notifyItemChanged(adapterPosition)
                }
                if (clickResult.contains(DebugDialog.ClickResult.GoUp)) {
                    adapter.goLevelUp()
                }
            }
        }
    }
}