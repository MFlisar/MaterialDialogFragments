package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.events.DialogFastAdapterEvent
import com.michaelflisar.dialogs.fastadapter.R
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogFastAdapter
import com.michaelflisar.text.Text
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogFastAdapterFragment<Item : IItem<*>> : MaterialDialogFragment<DialogFastAdapter<Item>>() {

    companion object {

        fun <Item : IItem<*>> create(setup: DialogFastAdapter<Item>): DialogFastAdapterFragment<Item> {
            val dlg = DialogFastAdapterFragment<Item>()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var lastFilter: String
    private lateinit var viewData: ViewData
    private lateinit var itemAdapter: ItemAdapter<Item>
    private lateinit var fastAdapter: FastAdapter<Item>

    @Suppress("UNCHECKED_CAST")
    protected val adapter: FastAdapter<Item>
        get() = viewData.rvData.adapter as FastAdapter<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastFilter = savedInstanceState.getString("lastFilter") ?: ""
        } else {
            lastFilter = ""
        }
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(activity!!, this, !setup.withToolbar)

        dialog.customView(
                if (setup.withToolbar) R.layout.dialog_recyclerview_toolbar else R.layout.dialog_recyclerview,
                scrollable = false,
                noVerticalPadding = setup.withToolbar
        )
                .positiveButton(setup) {
                    val selectionData = when (setup.selectionMode) {
                        DialogFastAdapter.SelectionMode.SingleClick -> null
                        DialogFastAdapter.SelectionMode.SingleSelect,
                        DialogFastAdapter.SelectionMode.MultiSelect -> getData()
                    }
                    onHandleClick(WhichButton.POSITIVE.ordinal, selectionData)
                    dismiss()
                }
                .noAutoDismiss()
                .negativeButton(setup) {
                    sendEvent(DialogFastAdapterEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                    dismiss()
                }
                .neutralButton(setup) {
                    sendEvent(DialogFastAdapterEvent(setup, WhichButton.NEUTRAL.ordinal, null))
                }

        val view = dialog.getCustomView()
        viewData = ViewData(view, setup)

        if (setup.withToolbar) {
            viewData.toolbar?.title = setup.title?.get(activity!!)
        }

        viewData.rvData.layoutManager = getLayoutManager()
        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        when (setup.selectionMode) {
            DialogFastAdapter.SelectionMode.SingleClick -> {
                fastAdapter.onClickListener = { _, _, item, position ->
                    val originalPosition = if (setup.filterPredicate != null) itemAdapter.itemList.items.indexOf(item) else position
                    if (isClickable(item, originalPosition)) {
                        val data = DialogFastAdapterEvent.Data(originalPosition, item)
                        onHandleClick(null, data)
                        dismiss()
                    }
                    true
                }
            }
            DialogFastAdapter.SelectionMode.SingleSelect,
            DialogFastAdapter.SelectionMode.MultiSelect -> {
                val selectExtension = fastAdapter.getSelectExtension()
                selectExtension.apply {
                    isSelectable = true
                    multiSelect = setup.selectionMode == DialogFastAdapter.SelectionMode.MultiSelect
                    selectWithItemUpdate = false
                }
            }
        }

        viewData.rvData.adapter = fastAdapter

        setup.filterPredicate?.let {
            itemAdapter.itemFilter.filterPredicate = { item: Item, constraint: CharSequence? ->
                it.filter(item, constraint?.toString() ?: "")
            }

            viewData.svSearch.visibility = View.VISIBLE
            viewData.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    lastFilter = query ?: ""
                    itemAdapter.filter(lastFilter)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    lastFilter = query ?: ""
                    itemAdapter.filter(lastFilter)
                    return true
                }
            })
            if (lastFilter.isNotEmpty()) {
//                itemAdapter.filter(lastFilter)
                viewData.svSearch.setQuery(lastFilter, false)
            }
        }

        val selectedIndizes = if (savedInstanceState?.containsKey("selectedIndizes") == true) savedInstanceState.getIntegerArrayList("selectedIndizes")!! else emptyList<Int>()
        if (setup.itemProvider.loadItemsAsynchronous) {
            viewData.pbLoading.visibility = View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val items = setup.itemProvider.loadItems(requireContext())
                    withContext(Dispatchers.Main) {
                        updateItems(items, selectedIndizes)
                    }
                }
            }
        } else {
            val items = setup.itemProvider.loadItems(requireContext())
            updateItems(items, selectedIndizes)
        }

        updateInfo(setup.info)

        return dialog
    }

    private fun updateItems(items: List<Item>, selectedIndizes: List<Int>) {
        viewData.pbLoading.visibility = View.GONE
        itemAdapter.setNewList(items, false)
        if (setup.filterPredicate != null && lastFilter.isNotEmpty()) {
            itemAdapter.filter(lastFilter)
        }
        if (setup.selectionMode != DialogFastAdapter.SelectionMode.SingleClick && selectedIndizes.isNotEmpty()) {
            val selectExtension = fastAdapter.getSelectExtension()
            selectExtension.select(selectedIndizes)
        }
    }

    private fun updateInfo(info: Text?) {
        val infoText = info?.get(requireActivity())
        if (infoText?.length ?: 0 > 0) {
            viewData.tvInfo.visibility = View.VISIBLE
            viewData.tvInfo.text = infoText
            if (setup.infoSize != null) {
                viewData.tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, setup.infoSize!!)
            }
        } else {
            viewData.tvInfo.visibility = View.GONE
        }
    }

    private fun getData(): DialogFastAdapterEvent.Data<Item> {
        val selectExtension = fastAdapter.getSelectExtension()
        val allItems = itemAdapter.itemList.items
        val items = selectExtension.selectedItems.toList()
        val indizes = items.map { allItems.indexOf(it) }
        return DialogFastAdapterEvent.Data(indizes, items)
    }

    private fun onHandleClick(buttonIndex: Int?, data: DialogFastAdapterEvent.Data<Item>?) {
        sendEvent(DialogFastAdapterEvent(setup, buttonIndex, data))
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    private fun isClickable(item: Item, pos: Int): Boolean {
        return true
    }

    protected fun updateData(items: ArrayList<Item>) {
        itemAdapter.setNewList(items)
        if (lastFilter.isNotEmpty()) {
            itemAdapter.itemFilter.filter(lastFilter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    protected fun addItem(item: Item) {
        itemAdapter.add(item)
    }

    protected fun removeItem(item: Item): Int {
        val index = itemAdapter.itemList.items.indexOf(item)
        itemAdapter.remove(index)
        return index
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (setup.filterPredicate != null) {
            lastFilter = viewData.svSearch.query.toString()
            if (lastFilter.isNotEmpty()) {
                outState.putString("lastFilter", lastFilter)
            }
        }
        if (setup.selectionMode != DialogFastAdapter.SelectionMode.SingleClick) {
            val data = getData()
            outState.putIntegerArrayList("selectedIndizes", ArrayList(data.indizes))
        }
        super.onSaveInstanceState(outState)
    }

    class ViewData(view: View, setup: DialogFastAdapter<*>) {
        val toolbar: Toolbar? = if (setup.withToolbar) view.findViewById(R.id.toolbar) else null
        val rvData: RecyclerView = view.findViewById(R.id.rvData)
        val llLoading: LinearLayout = view.findViewById(R.id.llLoading)
        val pbLoading: ProgressBar = view.findViewById(R.id.pbLoading)
        val tvLoading: TextView = view.findViewById(R.id.tvLoading)
        val svSearch: SearchView = view.findViewById(R.id.svSearch)
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
    }
}
