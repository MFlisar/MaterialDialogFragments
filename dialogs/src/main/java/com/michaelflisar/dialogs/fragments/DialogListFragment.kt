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
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.events.DialogListEvent
import com.michaelflisar.dialogs.interfaces.ITextImageProvider
import com.michaelflisar.dialogs.message
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogList
import com.michaelflisar.dialogs.title

open class DialogListFragment : BaseDialogFragment() {

    companion object {

        fun create(setup: DialogList): DialogListFragment {
            val dlg = DialogListFragment()
            val args = Bundle().apply {
                putParcelable("setup", setup)
            }
            dlg.arguments = args
            return dlg
        }

//        fun createString(id: Int, title: Any, text: Any?, posButton: Any, multiClick: Boolean, items: ArrayList<String>): DialogListFragment {
//            val dlg = DialogListFragment()
//            val args = Bundle().apply {
//                putInt("id", id)
//                putInt("type", Type.String.ordinal)
//                putResOrString("title", title)
//                putResOrString("label", text)
//                putResOrString("posButton", posButton)
//                putBoolean("multiClick", multiClick)
//                putStringArrayList("items", items)
//                putInt("iconSize", IconSize.Small.ordinal)
//                putBoolean("multiSelect", false)
//            }
//            dlg.arguments = args
//            return dlg
//        }
//
//        fun createString(id: Int, title: Any, text: Any?, posButton: Any, multiClick: Boolean, items: ArrayList<String>, icons: ArrayList<Int>): DialogListFragment {
//            val dlg = DialogListFragment()
//            val args = Bundle().apply {
//                putInt("id", id)
//                putInt("type", Type.StringWithImage.ordinal)
//                putResOrString("title", title)
//                putResOrString("label", text)
//                putResOrString("posButton", posButton)
//                putBoolean("multiClick", multiClick)
//                putStringArrayList("items", items)
//                putIntegerArrayList("icons", icons)
//                putInt("iconSize", IconSize.Small.ordinal)
//                putBoolean("multiSelect", false)
//            }
//            dlg.arguments = args
//            return dlg
//        }
//
//        fun <T : Parcelable> createParcelable(id: Int, title: Any, text: Any?, posButton: Any, multiClick: Boolean, items: ArrayList<T>): DialogListFragment {
//            val dlg = DialogListFragment()
//            val args = Bundle().apply {
//                putInt("id", id)
//                putInt("type", Type.Parcelable.ordinal)
//                putResOrString("title", title)
//                putResOrString("label", text)
//                putResOrString("posButton", posButton)
//                putBoolean("multiClick", multiClick)
//                putParcelableArrayList("items", items)
//                putInt("iconSize", IconSize.Small.ordinal)
//                putBoolean("multiSelect", false)
//            }
//            dlg.arguments = args
//            return dlg
//        }
    }

    private lateinit var setup: DialogList

    private var mAdapter: TextImageRVAdapter? = null

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = getSetup()

//        val id = XXX.getInt("id")
//        val title = XXX.getResOrString("title")
//        val text = XXX.getResOrString("label")
//        val posButton = XXX.getResOrString("posButton")
//        val multiClick = XXX.getBoolean("multiClick")
//        val type = Type.values()[XXX.getInt("type")]
//        val multiSelect = XXX.getBoolean("multiSelect")
//        val checkMark = if (XXX.containsKey("checkMark")) XXX.getInt("checkMark") else null
//        val singleSelected = if (XXX.containsKey("singleSelected")) XXX.getInt("singleSelected") else null
//        val onlyShowIconIfSelected = if (XXX.containsKey("onlyShowIconIfSelected")) XXX.getBoolean("onlyShowIconIfSelected") else null
//        val hideDefaultCheckMark = if (XXX.containsKey("hideDefaultCheckMark")) XXX.getBoolean("hideDefaultCheckMark") else null
//        val initialMultiSelection = if (XXX.containsKey("initialMultiSelection")) XXX.getIntegerArrayList("initialMultiSelection") else null
//        val noImageVisibilityGone = if (XXX.containsKey("noImageVisibilityGone")) XXX.getBoolean("noImageVisibilityGone") else null

        // prepare item array
        val itemArray: List<Any> = setup.items.map {
            when (it) {
                is DialogList.Item.Simple -> {
                    it.text ?: ""
                }
                is DialogList.Item.SimpleWithIcon -> {
                    TextImageItem(it.icon ?: 0, it.text ?: "")
                }
                is DialogList.Item.Custom -> {
                    it.parcelable
                }
            }
        }

        return internalOnCreateDialog(savedInstanceState, setup, itemArray)
    }

    protected fun internalOnCreateDialog(savedInstanceState: Bundle?, setup: DialogList, itemArray: List<Any>): Dialog {
        var dialog = MaterialDialog(activity!!)
                .cancelable(true)
                .noAutoDismiss()

        dialog = onSetCallback(savedInstanceState, setup, itemArray, dialog)

        dialog.title(setup.title)
        dialog.positiveButton(setup.posButton)

        setup.text?.let {
            dialog.message(it)
        }

        dialog = onSetAdapterOrItems(savedInstanceState, setup, itemArray, dialog)

        dialog.getRecyclerView().let {
            it.setVerticalScrollBarEnabled(true)
            it.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY)
        }

        return dialog
    }

    protected open fun onSetCallback(savedInstanceState: Bundle?, setup: DialogList, itemArray: List<Any>, dialog: MaterialDialog): MaterialDialog {
        dialog.positiveButton {
            if (setup.selectionMode == DialogList.SelectionMode.Multi) {
                if (mAdapter != null) {
                    val indizes = mAdapter!!.selection.toList()
                    val items = indizes.map { itemArray[it] }
                    sendEvent(DialogListEvent(setup, indizes, items))
                }
            }
            dismiss()
        }
        return dialog
    }

    @SuppressLint("CheckResult")
    protected open fun onSetAdapterOrItems(savedInstanceState: Bundle?, setup: DialogList, itemArray: List<Any>, dialog: MaterialDialog): MaterialDialog {

        if (itemArray.size == 0) {
            // create an empty dialog, type of list does not matter
            dialog
                    .listItems(
                            items = arrayListOf(),
                            waitForPositiveButton = false,
                            selection = { _: MaterialDialog, index: Int, _: String ->
                                sendEvent(DialogListEvent(setup, index, itemArray[index]))
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
                            sendEvent(DialogListEvent(setup, pos, itemArray[pos]))
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
                                            sendEvent(DialogListEvent(setup, index.toList(), item))
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
                                            sendEvent(DialogListEvent(setup, index, itemArray[index]))
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
