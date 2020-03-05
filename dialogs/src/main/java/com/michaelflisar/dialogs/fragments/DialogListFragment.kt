package com.michaelflisar.dialogs.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.adapters.TextImageItem
import com.michaelflisar.dialogs.adapters.TextImageRVAdapter
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.events.DialogListEvent
import com.michaelflisar.dialogs.interfaces.ITextImageProvider
import com.michaelflisar.dialogs.setups.DialogList

open class DialogListFragment : BaseDialogFragment<DialogList>() {

    companion object {

        fun create(setup: DialogList): DialogListFragment {
            val dlg = DialogListFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private var mAdapter: TextImageRVAdapter? = null

    private fun getItems() = setup.items

    fun updateItems(items: List<DialogList.Item>, setupUpdater: ((setup: DialogList) -> DialogList)? = null) {

        var newSetup = setup.copy(items = items)
        setupUpdater?.let {
            newSetup = it.invoke(newSetup)
        }
        setSetupArgs(newSetup)

        dialog?.let {
            val itemArray = createItems()
            onSetAdapterOrItems(null, itemArray, it as MaterialDialog)
        }
    }

    final override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        val items = createItems()
        val dialog = internalOnCreateDialog(savedInstanceState, items)
        onUpdateDialog(dialog)
        return dialog
    }

    protected open fun onUpdateDialog(dialog: Dialog) {
        // empty...
    }

    protected open fun createItems(): List<Any> {
        return setup.items.map {
            when (it) {
                is DialogList.Item.Simple -> {
                    it.text ?: ""
                }
                is DialogList.Item.SimpleWithIcon -> {
                    TextImageItem(it.icon, it.text ?: "")
                }
                is DialogList.Item.Custom -> {
                    it.parcelable
                }
            }
        }
    }

    private fun internalOnCreateDialog(savedInstanceState: Bundle?, itemArray: List<Any>): Dialog {
        var dialog = MaterialDialog(activity!!)
            .cancelable(true)
            .noAutoDismiss()

        dialog = onSetCallback(savedInstanceState, itemArray, dialog)

        setup.title?.let {
            dialog.title(it)
        }

        dialog.positiveButton(setup.posButton)

        setup.negButton?.let {
            dialog.negativeButton(it) {
                sendEvent(DialogListEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                dismiss()
            }
        }

        setup.neutrButton?.let {
            dialog.neutralButton(it) {
                sendEvent(DialogListEvent(setup, WhichButton.NEUTRAL.ordinal, null))
            }
        }

        setup.text?.let {
            dialog.message(it)
        }

        dialog = onSetAdapterOrItems(savedInstanceState, itemArray, dialog)

        dialog.getRecyclerView().let {
            it.setVerticalScrollBarEnabled(true)
            it.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY)
        }

        return dialog
    }

    protected open fun onSetCallback(
        savedInstanceState: Bundle?,
        itemArray: List<Any>,
        dialog: MaterialDialog
    ): MaterialDialog {
        dialog.positiveButton {
            if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                if (mAdapter != null) {
                    val indizes = mAdapter!!.selection.toList()
                    val items = indizes.map { itemArray[it] }
                    sendEvent(
                        DialogListEvent(
                            setup,
                            WhichButton.POSITIVE.ordinal,
                            DialogListEvent.Data(indizes, items)
                        )
                    )
                }
            }
            dismiss()
        }
        return dialog
    }

    @SuppressLint("CheckResult")
    protected open fun onSetAdapterOrItems(
        savedInstanceState: Bundle?,
        itemArray: List<Any>,
        dialog: MaterialDialog
    ): MaterialDialog {

        if (itemArray.size == 0) {
            // create an empty dialog, type of list does not matter
            dialog
                .listItems(
                    items = arrayListOf(),
                    waitForPositiveButton = false,
                    selection = { _: MaterialDialog, index: Int, _: String ->
                        sendEvent(DialogListEvent(setup, null, DialogListEvent.Data(index, itemArray[index])))
                    })
        } else {
            val item = itemArray.first()

            when (item) {
                is ITextImageProvider -> {

                    var initialSelection = HashSet<Int>()
                    if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                        if (savedInstanceState != null) {
                            @Suppress("UNCHECKED_CAST")
                            initialSelection = savedInstanceState.getSerializable("selection") as HashSet<Int>
                        } else {
                            initialSelection.addAll(setup.initialMultiSelection.toList())
                        }
                    } else if (setup.initialSingleSelection != null) {
                        initialSelection.add(setup.initialSingleSelection!!)
                    }

                    @Suppress("UNCHECKED_CAST")
                    mAdapter = TextImageRVAdapter(
                        itemArray as List<ITextImageProvider>,
                        false,
                        setup.iconSize,
                        imageColorFilterColor = setup.iconColorTint,
                        imageColorFilterMode = setup.iconColorTintMode,
                        noImageVisibility = setup.noImageVisibility,
                        onlyShowIconIfSelected = setup.onlyShowIconIfItemIsSelected,
                        selection = initialSelection,
                        hideDefaultCheckMark = setup.hideDefaultCheckMarkIcon,
                        checkMark = setup.checkMark,
                        mode = setup.selectionMode
                    ) { _, _, _, pos ->
                        if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                            mAdapter!!.toggleMultiSelect(pos)
                            mAdapter!!.notifyItemChanged(pos)
                        } else {
                            sendEvent(DialogListEvent(setup, null, DialogListEvent.Data(pos, itemArray[pos])))
                            if (!setup.multiClick) {
                                dismiss()
                            }
                        }
                    }

                    val lm = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    dialog.customListAdapter(mAdapter!!)
                    dialog.getRecyclerView().layoutManager = lm
                }
                else -> {
                    val stringItems = arrayListOf<String>()
                    if (item is String)
                        stringItems.addAll(itemArray as List<String>)
                    else
                        stringItems.addAll(itemArray.map { it.toString() })

                    if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                        dialog
                            .listItemsMultiChoice(
                                items = stringItems,
                                initialSelection = setup.initialMultiSelection,
                                allowEmptySelection = true,
                                selection = { _: MaterialDialog, index: IntArray, item: List<String> ->
                                    sendEvent(DialogListEvent(setup, null, DialogListEvent.Data(index.toList(), item)))
                                    if (!setup.multiClick) {
                                        dismiss()
                                    }
                                })
                    } else {
                        dialog
                            .listItems(
                                items = stringItems,
                                waitForPositiveButton = false,
                                selection = { _: MaterialDialog, index: Int, _: String ->
                                    sendEvent(
                                        DialogListEvent(
                                            setup,
                                            null,
                                            DialogListEvent.Data(index, itemArray[index])
                                        )
                                    )
                                    if (!setup.multiClick) {
                                        dismiss()
                                    }
                                })
                    }
                }
            }
        }
        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mAdapter?.mode == DialogList.SelectionMode.Multi) {
            outState.putSerializable("selection", mAdapter!!.selection)
        }
    }
}
