package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.events.DialogNumberEvent
import com.michaelflisar.dialogs.helper.RepeatListener
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.positiveButton
import com.michaelflisar.dialogs.setups.DialogNumberPicker
import com.michaelflisar.dialogs.title
import java.util.*

open class DialogNumberPickerFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {

        fun create(setup: DialogNumberPicker): DialogNumberPickerFragment {
            val dlg = DialogNumberPickerFragment()
            val args = Bundle().apply {
                putParcelable("setup", setup)
            }
            dlg.arguments = args
            return dlg
        }
    }

    private var viewDatas = arrayListOf<ViewAndData>()

    protected open val layout: Int
        get() = R.layout.dialog_number_integer_picker

    protected open val rowNumberLayoutId: Int
        get() = R.layout.view_row_number

    private lateinit var setup: DialogNumberPicker
    private var values: ArrayList<Int> = arrayListOf()

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = arguments!!.getParcelable("setup")!!

        values.clear()
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("values"))
                values = savedInstanceState.getIntegerArrayList("values")!!
        } else {
            values.add(setup.initialValue)
            setup.additonalValues.forEach { values.add(it.initialValue) }
        }
        viewDatas.clear()
        for (i in 1..values.size) {
            viewDatas.add(ViewAndData())
            viewDatas[i - 1].setValue(values[i - 1])
        }

        val dialog = MaterialDialog(activity!!)
                .customView(layout, scrollable = values.size > 1)
                .positiveButton {
                    sendEvent(DialogNumberEvent(extra, setup.id, getEventValues(viewDatas)))
                    dismiss()
                }
                .cancelable(true)

        dialog.title(setup.title)
        dialog.positiveButton(setup.posButton)
        setup.negButton?.let {
            dialog.negativeButton(it)
        }

        val view = dialog.getCustomView()
        updateView(view)

        val repeatListener = RepeatListener(400L, 100L, this)

        for (i in 1..values.size) {
            var v = view
            if (i > 1) {
                v = LayoutInflater.from(activity).inflate(rowNumberLayoutId, null, false)
                (view as ViewGroup).addView(v)
            }
            val text = if (i == 1) setup.text else setup.additonalValues[i - 2].label
            viewDatas[i - 1].initViews(activity!!, text, i - 1, v, repeatListener)
            updateRowView(i - 1, v)
        }

        updateValue()

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val vals = ArrayList(viewDatas.map { it.value })
        outState.putIntegerArrayList("values", vals)
    }

    protected open fun updateView(view: View) {

    }

    protected open fun updateRowView(index: Int, view: View) {

    }

    protected fun updateValue() {
        for (data in viewDatas)
            data.updateValue()
        onValueChanged()
    }

    protected open fun onValueChanged() {

    }

    protected fun getValue(index: Int): Int {
        return viewDatas[index].value
    }

    override fun onClick(v: View) {
        val index = v.getTag(R.id.tag_index) as Int
        viewDatas[index].onClick(v)
    }

    fun getEventValues(data: List<ViewAndData>): List<Int> {
        val values = ArrayList<Int>()
        for (i in data.indices)
            values.add(data[i].value)
        return values
    }

    inner class ViewAndData {
        internal var tvText: TextView? = null
        internal var tvNumber: TextView? = null
        internal var tvSubtract: TextView? = null
        internal var tvAdd: TextView? = null

        internal var value: Int = 0

        fun initViews(context: Context, text: Text?, index: Int, view: View, repeatListener: RepeatListener) {
            tvAdd = view.findViewById<View>(R.id.tvAdd) as TextView
            tvSubtract = view.findViewById<View>(R.id.tvSubtract) as TextView
            tvNumber = view.findViewById<View>(R.id.tvNumber) as TextView
            tvText = view.findViewById<View>(R.id.tvText) as TextView

            tvAdd!!.setTag(R.id.tag_index, index)
            tvSubtract!!.setTag(R.id.tag_index, index)

            tvAdd!!.setOnTouchListener(repeatListener)
            tvSubtract!!.setOnTouchListener(repeatListener)

            updateText(context, text)
        }

        private fun updateText(context: Context, text: Text?) {
            val t = text?.get(context)
            if (t != null)
                tvText!!.text = t
            else
                tvText!!.visibility = View.GONE
        }

        fun updateValue() {
            if (setup.valueFormatRes == null)
                tvNumber!!.text = value.toString()
            else
                tvNumber!!.text = getString(setup.valueFormatRes!!, value)
        }

        internal fun onClick(v: View) {
            if (v.id == R.id.tvAdd) {
                if (setup.max == null || value < setup.max!!) {
                    value += setup.step
                    updateValue()
                    onValueChanged()
                }
            } else if (v.id == R.id.tvSubtract) {
                if (setup.min == null || value > setup.min!!) {
                    value -= setup.step
                    updateValue()
                    onValueChanged()
                }
            }
        }

        fun setValue(v: Int) {
            value = v
        }

        fun reset() {
            tvAdd = null
            tvSubtract = null
            tvNumber = null

        }
    }
}
