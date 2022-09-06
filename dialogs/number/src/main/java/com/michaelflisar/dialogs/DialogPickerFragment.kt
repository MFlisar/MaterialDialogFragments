package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.michaelflisar.dialogs.classes.RepeatListener
import com.michaelflisar.dialogs.number.R
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding
import kotlinx.parcelize.Parcelize

class DialogPickerFragment<T : Number> :
    MaterialDialogFragment<DialogPickerFragment<T>, DialogPicker<T>, MdfContentNumberBinding>() {

    companion object {
        fun <T : Number> create(
            setup: DialogPicker<T>,
            dialog: Boolean
        ): DialogPickerFragment<T> {
            return DialogPickerFragment<T>().apply {
                arguments = MaterialDialogFragmentUtil.createArguments(this, setup, dialog)
            }
        }
    }

    override val wrapInScrollContainer = true

    override fun createContentBinding(
        layoutInflater: LayoutInflater
    ): MdfContentNumberBinding {
        return MdfContentNumberBinding.inflate(layoutInflater)
    }

    override fun initContentBinding(binding: MdfContentNumberBinding, savedInstanceState: Bundle?) {
        value = state?.value ?: setup.value
        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }
       updateDisplayValue()

        val repeatListener = RepeatListener(400L, 100L) {
            value = setup.adjust(getCurrentInput(), it.id == R.id.mdf_increase)
            updateDisplayValue()
        }
        binding.mdfIncrease.setOnTouchListener(repeatListener)
        binding.mdfDecrease.setOnTouchListener(repeatListener)
    }

    // -------------
    // lifecycle
    // -------------

    private var state: State<T>? = null
    private lateinit var value: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
            state = savedInstanceState.getParcelable(KEY_VIEW_STATE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_VIEW_STATE, State(value))
    }

    // -------------
    // functions
    // -------------

    internal fun getCurrentInput(): T {
        return value
    }

    private fun updateDisplayValue() {
        binding.mdfNumber.text = setup.setup.formatter?.format(requireContext(), value) ?: value.toString()
    }

    // -------------
    // State
    // -------------

    @Parcelize
    class State<T: Number>(
        val value: T
    ) : Parcelable
}