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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.text.Text
import com.michaelflisar.dialogs.events.DialogFastAdapterEvent
import com.michaelflisar.dialogs.fastadapter.R
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogFastAdapter
import com.michaelflisar.dialogs.title
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import java.util.*

abstract class DialogFastAdapterFragment : MaterialDialogFragment<DialogFastAdapter>() {

    companion object {

        fun <T : DialogFastAdapterFragment> create(setup: DialogFastAdapter, createFragment: (() -> T)): T {
            val dlg = createFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    protected var toolbar: Toolbar? = null
    protected var rvData: RecyclerView? = null
    protected var llLoading: LinearLayout? = null
    protected var pbLoading: ProgressBar? = null
    protected var tvLoading: TextView? = null
    protected var svSearch: SearchView? = null
    protected var data: ArrayList<IItem<*>>? = null
        private set
    protected var itemAdapter: ItemAdapter<IItem<*>>? = null
    private var lastFilter: String? = null

    @Suppress("UNCHECKED_CAST")
    protected val adapter: FastAdapter<IItem<*>>
        get() = rvData!!.adapter as FastAdapter<IItem<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = data

        if (savedInstanceState != null) {
            lastFilter = savedInstanceState.getString("lastFilter")
        }
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(activity!!, this, !setup.internalSetup.withToolbar)

        dialog.customView(
                if (setup.internalSetup.withToolbar) R.layout.dialog_recyclerview_toolbar else R.layout.dialog_recyclerview,
                scrollable = false,
                noVerticalPadding = setup.internalSetup.withToolbar
        )
                .positiveButton(setup) {
                    onHandleClick(null, WhichButton.POSITIVE.ordinal, null)
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

        updateBuilder(dialog)
        val view = dialog.getCustomView()

        toolbar = null
        if (setup.internalSetup.withToolbar) {
            toolbar = view.findViewById(R.id.toolbar)
        }
        rvData = view.findViewById(R.id.rvData)
        llLoading = view.findViewById(R.id.llLoading)
        pbLoading = view.findViewById(R.id.pbLoading)
        tvLoading = view.findViewById(R.id.tvLoading)
        svSearch = view.findViewById(R.id.svSearch)

        if (setup.internalSetup.withToolbar) {
            toolbar!!.setTitle(setup.title?.get(activity!!))
        }

        rvData!!.layoutManager = getLayoutManager()
        itemAdapter = ItemAdapter<IItem<*>>()
        val fastAdapter = FastAdapter.with(itemAdapter!!)

        if (setup.internalSetup.clickable) {
            fastAdapter.onClickListener = { _, _, item, position ->
                val originalPosition = if (setup.internalSetup.filterable) data!!.indexOf(item) else position
                if (isClickable(item, originalPosition)) {
                    onHandleClick(item, null, originalPosition)
                    if (setup.internalSetup.dismissOnClick) {
                        dismiss()
                    }
                }
                true
            }
        }
        onUpdateAdapter(itemAdapter!!)
        rvData!!.adapter = fastAdapter
        data = createData()
        itemAdapter!!.add(data!!)

        updateInfo(setup.internalSetup.info, view)
        onViewCreated(view, itemAdapter!!)

        if (setup.internalSetup.filterable) {
            try {
                @Suppress("UNCHECKED_CAST")
                itemAdapter!!.itemFilter.filterPredicate = { item: IItem<*>, constraint: CharSequence? ->
                    (this as IPredicate<IItem<*>>).filter(item, constraint)
                }
            } catch (e: ClassCastException) {
                throw RuntimeException("Filterable adapter must implement IPredicate<IItem<*>>!")
            }

            svSearch!!.visibility = View.VISIBLE
            svSearch!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    lastFilter = query ?: ""
                    itemAdapter!!.filter(lastFilter)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    lastFilter = query ?: ""
                    itemAdapter!!.filter(lastFilter)
                    return true
                }
            })
            if (lastFilter != null) {
                svSearch!!.setQuery(lastFilter, false)
            }
        }

        return dialog
    }

    protected fun updateInfo(info: Text?, view: View) {
        val infoText = info?.get(activity!!)
        val tvInfo = view.findViewById<TextView>(R.id.tvInfo)
        if (infoText?.length ?: 0 > 0) {
            tvInfo.visibility = View.VISIBLE
            tvInfo.text = infoText
            if (setup.internalSetup.infoSize != null) {
                tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, setup.internalSetup.infoSize!!)
            }
        } else {
            tvInfo.visibility = View.GONE
        }
    }

    protected open fun onHandleClick(item: IItem<*>?, buttonIndex: Int?, position: Int?) {
        val data = if (item != null && position != null) {
            DialogFastAdapterEvent.Data(item, position)
        } else null
        sendEvent(DialogFastAdapterEvent(setup, buttonIndex, data))
    }

    protected open fun onUpdateAdapter(adapter: ItemAdapter<IItem<*>>) {

    }

    protected open fun updateBuilder(dialog: MaterialDialog) {

    }

    protected open fun onViewCreated(view: View, adapter: ItemAdapter<IItem<*>>) {

    }

    protected open fun onPositiveClicked() {

    }

    protected open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    protected open fun isClickable(item: IItem<*>, pos: Int): Boolean {
        return true
    }

    protected open fun updateData(items: ArrayList<IItem<*>>) {
        data = items
        itemAdapter!!.setNewList(data!!)

        if (lastFilter != null && lastFilter!!.length > 0) {
            itemAdapter!!.itemFilter.filter(lastFilter)
        }
    }

    override fun onDestroyView() {
        rvData = null
        llLoading = null
        rvData = null
        tvLoading = null
        super.onDestroyView()
    }

    protected fun addItem(item: IItem<*>) {
        data!!.add(item)
        itemAdapter!!.add(item)
    }

    protected fun removeItem(item: IItem<*>): Int {
        val index = data!!.indexOf(item)
        data!!.removeAt(index)
        itemAdapter!!.remove(index)
        return index
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (setup.internalSetup.filterable) {
            lastFilter = svSearch!!.query.toString()
            if (lastFilter != null && lastFilter!!.length > 0) {
                outState.putString("lastFilter", lastFilter)
            }
        }
    }

    protected abstract fun createData(): ArrayList<IItem<*>>

    interface IPredicate<Item : IItem<*>> {
        fun filter(item: Item, constraint: CharSequence?): Boolean
    }
}
