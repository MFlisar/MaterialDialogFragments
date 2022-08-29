package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.ListItemAdapter
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

        val hasDescription = setup.description.display(binding.mdfDescription).isNotEmpty()
        binding.mdfDescription.visibility = if (hasDescription) View.VISIBLE else View.GONE

        binding.mdfDividerTop.alpha = 0f
        binding.mdfDividerBottom.alpha = 0f

        when (val itemsProvider = setup.itemsProvider) {
            is DialogList.ItemProvider.ItemLoader -> {
                // load items
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val items = itemsProvider.loader.load(requireContext())
                        withContext(Dispatchers.Main) {
                            updateItems(items)
                        }
                    }
                }
            }
            is DialogList.ItemProvider.List -> {
                updateItems(itemsProvider.items)
            }
        }

        binding.mdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = this@DialogListFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    checkDividers()
                }
            })
        }

        binding.mdfContainerFilter.visibility =
            if (setup.filter == null) View.GONE else View.VISIBLE

        binding.mdfTextInputEditText.setText(state?.filter ?: "")
        binding.mdfTextInputEditText.doOnTextChanged { text, start, before, count ->
            adapter.updateFilter(text?.toString() ?: "")
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
            requireContext(),
            ListItemAdapter.Setup(setup),
            state?.filter ?: "",
            state?.selectedIds?.toMutableSet() ?: HashSet()
        ) { _, item ->
            when (setup.selectionMode) {
                DialogList.SelectionMode.SingleSelect -> {
                    val selectedId = adapter.getCheckedIds().firstOrNull()
                    if (selectedId == null)
                        adapter.setItemChecked(item, true)
                    else {
                        if (selectedId != item.id)
                            adapter.setItemChecked(item, false)
                        adapter.toggleItemChecked(item)
                    }
                }
                DialogList.SelectionMode.MultiSelect -> {
                    adapter.toggleItemChecked(item)
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            KEY_VIEW_STATE,
            State(getSelectedIds(), binding.mdfTextInputEditText.text?.toString() ?: "")
        )
    }

    // -------------
    // functions
    // -------------

    private fun updateItems(items: List<DialogList.ListItem>) {
        binding.mdfLoading.visibility = View.GONE
        adapter.updateItems(items)
        binding.mdfRecyclerView.post {
            checkDividers()
        }
    }

    private fun getSelectedIds(): Set<Int> {
        return adapter.getCheckedIds()
    }

    internal fun getSelectedItemsForResult(): List<DialogList.ListItem> {
        return adapter.getCheckedItemsForResult()
    }

    private fun checkDividers() {
        val alphaTop = if (!binding.mdfRecyclerView.canScrollVertically(-1)) 0f else 1f
        val alphaBottom = if (!binding.mdfRecyclerView.canScrollVertically(1)) 0f else 1f
        binding.mdfDividerTop.animate().cancel()
        binding.mdfDividerTop.animate().alpha(alphaTop).start()
        binding.mdfDividerBottom.animate().cancel()
        binding.mdfDividerBottom.animate().alpha(alphaBottom).start()
    }

    // -------------
    // State
    // -------------

    @Parcelize
    class State(
        val selectedIds: Set<Int>,
        val filter: String
    ) : Parcelable
}