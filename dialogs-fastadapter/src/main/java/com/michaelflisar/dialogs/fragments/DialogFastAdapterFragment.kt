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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.events.DialogFastAdapterEvent
import com.michaelflisar.dialogs.fastadapter.R
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogFastAdapter
import com.michaelflisar.dialogs.title
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import java.util.*

abstract class DialogFastAdapterFragment : BaseDialogFragment() {

    companion object {

        fun <T : DialogFastAdapterFragment> create(setup: DialogFastAdapter, createFragment: (() -> T)): T {
            val dlg = createFragment()
            val args = Bundle().apply {
                putParcelable("setup", setup)
            }
            dlg.arguments = args
            return dlg
        }
    }

    protected var toolbar: Toolbar? = null
    protected var rvData: RecyclerView? = null
    protected var llLoading: LinearLayout? = null
    protected var pbLoading: ProgressBar? = null
    protected var tvLoading: TextView? = null
    protected var svSearch: SearchView? = null
    protected var data: ArrayList<IItem<*, *>>? = null
        private set
    protected var mAdapter: FastItemAdapter<IItem<*, *>>? = null
    private var lastFilter: String? = null

    @Suppress("UNCHECKED_CAST")
    protected val adapter: FastItemAdapter<IItem<*, *>>
        get() = rvData!!.adapter as FastItemAdapter<IItem<*, *>>

    private lateinit var setup: DialogFastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = data

        if (savedInstanceState != null) {
            lastFilter = savedInstanceState.getString("lastFilter")
        }
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = arguments!!.getParcelable("setup")!!

        val dialog = MaterialDialog(activity!!)
                .customView(if (setup.internalSetup.withToolbar) R.layout.dialog_recyclerview_toolbar else R.layout.dialog_recyclerview, scrollable = false)
                .positiveButton(setup.posButton) {
                    dismiss()
                }
                .cancelable(true)
                .noAutoDismiss()

        dialog.title(setup.title)
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
            toolbar!!.setTitle(setup.title.get(activity!!))
        }

        rvData!!.layoutManager = getLayoutManager()
        mAdapter = FastItemAdapter()
        if (setup.internalSetup.clickable) {
            mAdapter!!.withOnClickListener { _, _, item, position ->
                val originalPosition = if (setup.internalSetup.filterable) data!!.indexOf(item) else position
                if (isClickable(item, originalPosition)) {
                    onHandleClick(setup.id, item, originalPosition)
                    if (setup.internalSetup.dismissOnClick) {
                        dismiss()
                    }
                }
                true
            }
        }
        onUpdateAdapter(mAdapter!!)
        rvData!!.adapter = mAdapter
        data = createData()
        mAdapter!!.add(data)

        updateInfo(setup.internalSetup.info, view)
        onViewCreated(view, mAdapter!!)

        if (setup.internalSetup.filterable) {
            try {
                @Suppress("UNCHECKED_CAST")
                mAdapter!!.itemFilter.withFilterPredicate(this as IItemAdapter.Predicate<IItem<*, *>>)
            } catch (e: ClassCastException) {
                throw RuntimeException("Filterable adapter must implement IItemAdapter.Predicate<IItem>!")
            }

            svSearch!!.visibility = View.VISIBLE
            svSearch!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    lastFilter = query ?: ""
                    mAdapter!!.filter(lastFilter)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    lastFilter = query ?: ""
                    mAdapter!!.filter(lastFilter)
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

    protected open fun onHandleClick(id: Int, item: IItem<*, *>, position: Int) {
        sendEvent(DialogFastAdapterEvent(setup.extra, id, item, position))
    }

    protected open fun onUpdateAdapter(adapter: FastItemAdapter<IItem<*, *>>) {

    }

    protected open fun updateBuilder(dialog: MaterialDialog) {

    }

    protected open fun onViewCreated(view: View, adapter: FastItemAdapter<IItem<*, *>>) {

    }

    protected open fun onPositiveClicked() {

    }

    protected open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    protected open fun isClickable(item: IItem<*, *>, pos: Int): Boolean {
        return true
    }

    protected open fun updateData(items: ArrayList<IItem<*, *>>) {
        @Suppress("UNCHECKED_CAST")
        val adapter = rvData!!.adapter as FastItemAdapter<IItem<*, *>>?
        data = items
        adapter!!.setNewList(data)

        if (lastFilter != null && lastFilter!!.length > 0) {
            adapter.filter(lastFilter)
        }
    }

    override fun onDestroyView() {
        rvData = null
        llLoading = null
        rvData = null
        tvLoading = null
        super.onDestroyView()
    }

    protected fun addItem(item: IItem<*, *>) {
        data!!.add(item)
        adapter.add(item)
    }

    protected fun removeItem(item: IItem<*, *>): Int {
        val index = data!!.indexOf(item)
        data!!.removeAt(index)
        adapter.remove(index)
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

    protected abstract fun createData(): ArrayList<IItem<*, *>>
}
