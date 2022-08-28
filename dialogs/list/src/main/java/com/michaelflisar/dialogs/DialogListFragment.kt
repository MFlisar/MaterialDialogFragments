package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class DialogListFragment :
    MaterialDialogFragment<DialogListFragment, DialogList, MdfContentListBinding>() {

    companion object {
        fun create(
            setup: DialogList,
            dialog: Boolean
        ): DialogListFragment {
            return DialogListFragment().apply {
                arguments = MaterialDialogFragmentUtil.createArguments(this, setup, dialog)
            }
        }
    }

    // this dialog has a scrolling container itself!
    override val wrapInScrollContainer = false

    override fun createContentBinding(
        layoutInflater: LayoutInflater
    ): MdfContentListBinding {
        return MdfContentListBinding.inflate(layoutInflater)
    }

    override fun initContentBinding(binding: MdfContentListBinding, savedInstanceState: Bundle?) {
        setup.listDescription.display(binding.mdfDescription)

        binding.mdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = this@DialogListFragment.adapter
        }
        state?.let {
            // TODO...
        }
    }

    // -------------
    // lifecycle
    // -------------

    private var state: State? = null
    private lateinit var adapter: ListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
            state = savedInstanceState.getParcelable(KEY_VIEW_STATE)

        adapter = ListItemAdapter(
            setup.listItemsProvider.iconSize,
            setup.listSelectionMode,
            emptyList(),
            HashSet()
        ) { index, item ->
            when (setup.listSelectionMode) {
                DialogList.SelectionMode.SingleSelect -> {
                    val selectedIndex = adapter.getCheckedIndizes().firstOrNull()
                    if (selectedIndex == null)
                        adapter.setItemChecked(index, true)
                    else
                    {
                        if (selectedIndex != index)
                            adapter.setItemChecked(selectedIndex, false)
                        adapter.toggleItemChecked(index)
                    }
                }
                DialogList.SelectionMode.MultiSelect -> {
                    adapter.toggleItemChecked(index)
                }
                DialogList.SelectionMode.SingleClick -> {
                    setup.sendEvent(item)
                    dismiss()
                }
                DialogList.SelectionMode.MultiClick -> {
                    setup.sendEvent(item)
                }
            }
        }

        val itemsProvider = setup.listItemsProvider
        when (itemsProvider) {
            is DialogList.ItemProvider.ItemLoader -> {
                // load items
                lifecycleScope.launch(Dispatchers.IO) {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val items = itemsProvider.loader.load()
                        withContext(Dispatchers.Main) {
                            adapter.updateItems(items)
                        }
                    }
                }
            }
            is DialogList.ItemProvider.List -> {
                adapter.updateItems(itemsProvider.items)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_VIEW_STATE, State())
    }

    // -------------
    // functions
    // -------------

    internal fun getSelectedItems(): List<DialogList.ListItem> {
        return adapter.getCheckedItems()
    }

    // -------------
    // State
    // -------------

    @Parcelize
    class State(
        val selectedIndizes: List<Int>
    ) : Parcelable {
        constructor() : this(
            emptyList()
        )
    }
}