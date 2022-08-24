package com.michaelflisar.dialogs.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.michaelflisar.dialogs.adapters.TextImageItem
import com.michaelflisar.dialogs.adapters.TextImageRVAdapter
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.ITextImageProvider
import com.michaelflisar.dialogs.message
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogList

open class DialogListFragment : MaterialDialogFragment<DialogList>() {

    companion object {

        fun create(setup: DialogList): DialogListFragment {
            val dlg = DialogListFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private var mAdapter: TextImageRVAdapter? = null

    fun getItems() = setup.items

    fun updateItems(
        items: List<DialogList.Item>,
        setupUpdater: ((setup: DialogList) -> DialogList)? = null
    ) {

        var newSetup = setup.copy(items = items)
        setupUpdater?.let {
            newSetup = it.invoke(newSetup)
        }
        setSetupArgs(newSetup)

        dialog?.let {
            val itemArray = createItems()
            val dlg = it as MaterialDialog
            dlg.clearPositiveListeners()
            onSetCallback(null, itemArray, dlg)
            onSetAdapterOrItems(null, itemArray, dlg)
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

        // create dialog with correct style, title and cancelable flags
        var dialog = setup.createMaterialDialog(requireActivity(), this)

        dialog.noAutoDismiss()
        onSetCallback(savedInstanceState, itemArray, dialog)

        dialog
            .positiveButton(setup)
            .negativeButton(setup) {
                sendEvent(DialogList.Event.Empty(setup, MaterialDialogButton.Negative))
                dismiss()
            }
            .neutralButton(setup) {
                sendEvent(DialogList.Event.Empty(setup, MaterialDialogButton.Neutral))
            }

        setup.text?.let {
            dialog.message(it)
        }

        dialog = onSetAdapterOrItems(savedInstanceState, itemArray, dialog)

        dialog.getRecyclerView().let {
            it.isVerticalScrollBarEnabled = true
            it.scrollBarStyle = ListView.SCROLLBARS_OUTSIDE_OVERLAY
        }

        return dialog
    }

    protected open fun onSetCallback(
        savedInstanceState: Bundle?,
        itemArray: List<Any>,
        dialog: MaterialDialog
    ) {
        dialog.positiveButton {
            if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                if (mAdapter != null) {
                    val indizes = mAdapter!!.selection.toList()
                    val items = indizes.map { itemArray[it] }
                    sendEvent(
                        DialogList.Event.Data(
                            setup,
                            MaterialDialogButton.Positive,
                            indizes,
                            items
                        )
                    )
                }
            }
            dismiss()
        }
    }

    @SuppressLint("CheckResult")
    protected open fun onSetAdapterOrItems(
        savedInstanceState: Bundle?,
        itemArray: List<Any>,
        dialog: MaterialDialog
    ): MaterialDialog {

        if (itemArray.isEmpty()) {
            // create an empty dialog, type of list does not matter
            dialog
                .listItems(
                    items = arrayListOf(),
                    waitForPositiveButton = false,
                    selection = { _: MaterialDialog, index: Int, _: CharSequence ->
                        sendEvent(DialogList.Event.Data(setup, null, index, itemArray[index]))
                    })
        } else {
            val item = itemArray.first()

            when (item) {
                is ITextImageProvider -> {

                    var initialSelection = HashSet<Int>()
                    if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                        if (savedInstanceState != null) {
                            @Suppress("UNCHECKED_CAST")
                            initialSelection =
                                savedInstanceState.getSerializable("selection") as HashSet<Int>
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
                            sendEvent(DialogList.Event.Data(setup, null, pos, itemArray[pos]))
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
                                selection = { _: MaterialDialog, index: IntArray, item: List<CharSequence> ->
                                    sendEvent(
                                        DialogList.Event.Data(
                                            setup,
                                            null,
                                            index.toList(),
                                            item
                                        )
                                    )
                                    if (!setup.multiClick) {
                                        dismiss()
                                    }
                                })
                    } else {
                        dialog
                            .listItems(
                                items = stringItems,
                                waitForPositiveButton = false,
                                selection = { _: MaterialDialog, index: Int, _: CharSequence ->
                                    sendEvent(
                                        DialogList.Event.Data(
                                            setup,
                                            null,
                                            index,
                                            itemArray[index]
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
