package com.michaelflisar.dialogs.fastadapter

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
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import java.util.*

abstract class DialogFastAdapter : BaseDialogFragment() {

    companion object {

        fun <T : DialogFastAdapter> initBundle(dlg: T, id: Int, title: Int, posButton: Int): Bundle {
            val args = Bundle().apply {
                putInt("id", id)
                putInt("title", title)
                putInt("pos", posButton)

                // Standards setzen
                putBoolean("clickable", false)
                putBoolean("dismissOnClick", false)
                putInt("info", -1)
                putInt("infoSize", -1)
                putBoolean("filterable", false)
            }
            dlg.arguments = args
            return args
        }
    }

    protected var mDialog: MaterialDialog? = null
    protected var toolbar: Toolbar? = null
    protected var rvData: RecyclerView? = null
    protected var llLoading: LinearLayout? = null
    protected var pbLoading: ProgressBar? = null
    protected var tvLoading: TextView? = null
    protected var svSearch: SearchView? = null
    protected var data: ArrayList<IItem<*, *>>? = null
        private set
    protected var mAdapter: FastItemAdapter<IItem<*, *>>? = null
    private var mFilterable: Boolean = false
    private var mLastFilter: String? = null
    private var mInfoSize = -1

    @Suppress("UNCHECKED_CAST")
    protected val adapter: FastItemAdapter<IItem<*, *>>
        get() = rvData!!.adapter as FastItemAdapter<IItem<*, *>>

    @Suppress("UNCHECKED_CAST")
    fun <T : DialogFastAdapter> withClickable(dismissOnClick: Boolean): T {
        arguments!!.putBoolean("clickable", true)
        arguments!!.putBoolean("dismissOnClick", dismissOnClick)
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : DialogFastAdapter> withDetailText(info: Int, infoSize: Int): T {
        arguments!!.putInt("info", info)
        arguments!!.putInt("infoSize", infoSize)
        return this as T
    }

    /**
     * Dialog MUST implement [com.mikepenz.fastadapter.IItemAdapter.Predicate]&lt;[IItem]&gt; for this to work!
     *
     * @return this
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : DialogFastAdapter> withFilterable(): T {
        arguments!!.putBoolean("filterable", true)
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : DialogFastAdapter> withToolbar(): T {
        arguments!!.putBoolean("withToolbar", true)
        return this as T
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = data

        mFilterable = arguments!!.getBoolean("filterable")
        if (savedInstanceState != null) {
            mLastFilter = savedInstanceState.getString("mLastFilter")
        }

        mInfoSize = arguments!!.getInt("infoSize")
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id = arguments!!.getInt("id")
        val pos = arguments!!.getInt("pos")
        val title = arguments!!.getInt("title")
        val withToolbar = arguments!!.getBoolean("withToolbar")
        val clickable = arguments!!.getBoolean("clickable")
        val dismissOnClick = arguments!!.getBoolean("dismissOnClick")
        val info = arguments!!.getInt("info")
        val filterable = arguments!!.getBoolean("filterable")

        mDialog = MaterialDialog(activity!!)
                .customView(if (withToolbar) R.layout.dialog_recyclerview_toolbar else R.layout.dialog_recyclerview, scrollable = false)
                .positiveButton(pos) {
                    dismiss()
                }
                .cancelable(true)
                .noAutoDismiss()

        if (!withToolbar && title != -1) {
            mDialog!!.title(title)
        }

        updateBuilder(mDialog!!)

        val view = mDialog!!.getCustomView()

        toolbar = null
        if (withToolbar) {
            toolbar = view.findViewById(R.id.toolbar)
        }
        rvData = view.findViewById(R.id.rvData)
        llLoading = view.findViewById(R.id.llLoading)
        pbLoading = view.findViewById(R.id.pbLoading)
        tvLoading = view.findViewById(R.id.tvLoading)
        svSearch = view.findViewById(R.id.svSearch)

        if (withToolbar && title != -1) {
            toolbar!!.setTitle(title)
        }

        rvData!!.layoutManager = getLayoutManager()
        mAdapter = FastItemAdapter()
        if (clickable) {
            mAdapter!!.withOnClickListener { _, _, item, position ->
                val originalPosition = if (filterable) data!!.indexOf(item) else position
                if (isClickable(item, originalPosition)) {
                    onHandleClick(id, item, originalPosition)
                    if (dismissOnClick) {
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

        updateInfo(info, view)
        onViewCreated(view, mAdapter!!)

        if (mFilterable) {
            try {
                @Suppress("UNCHECKED_CAST")
                mAdapter!!.itemFilter.withFilterPredicate(this as IItemAdapter.Predicate<IItem<*, *>>)
            } catch (e: ClassCastException) {
                throw RuntimeException("Filterable adapter must implement IItemAdapter.Predicate<IItem>!")
            }

            svSearch!!.visibility = View.VISIBLE
            svSearch!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mLastFilter = query ?: ""
                    mAdapter!!.filter(mLastFilter)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    mLastFilter = query ?: ""
                    mAdapter!!.filter(mLastFilter)
                    return true
                }
            })
            if (mLastFilter != null) {
                svSearch!!.setQuery(mLastFilter, false)
            }
        }

        return mDialog!!
    }

    protected fun updateInfo(info: Int, view: View) {
        if (info == -1) {
            updateInfo(null, view)
        } else {
            updateInfo(view.context.getString(info), view)
        }
    }

    protected fun updateInfo(info: String?, view: View) {
        val tvInfo = view.findViewById<TextView>(R.id.tvInfo)
        if (info != null && info.length > 0) {
            tvInfo.visibility = View.VISIBLE
            tvInfo.text = info
            if (mInfoSize != -1) {
                tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInfoSize.toFloat())
            }
        } else {
            tvInfo.visibility = View.GONE
        }
    }

    protected open fun onHandleClick(id: Int, item: IItem<*, *>, position: Int) {
        sendEvent(DialogFastAdapterEvent(extra, id, item, position))
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

        if (mLastFilter != null && mLastFilter!!.length > 0) {
            adapter.filter(mLastFilter)
        }
    }

    override fun onDestroyView() {
        mDialog = null
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
        if (mFilterable) {
            mLastFilter = svSearch!!.query.toString()
            if (mLastFilter != null && mLastFilter!!.length > 0) {
                outState.putString("mLastFilter", mLastFilter)
            }
        }
    }

    protected abstract fun createData(): ArrayList<IItem<*, *>>

    class DialogFastAdapterEvent : BaseDialogEvent {
        private var item: IItem<*, *>? = null
        var index: Int = 0

        var neutral: Boolean = false

        constructor(extra: Bundle?, id: Int, item: IItem<*, *>?, index: Int) : super(extra, id) {
            this.item = item
            this.index = index

            neutral = false
        }

        constructor(id: Int) : super(null, id) {
            neutral = true
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : IItem<*, *>> getItem(): T? {
            return item as T?
        }
    }
}
