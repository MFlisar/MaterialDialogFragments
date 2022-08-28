package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import kotlinx.parcelize.Parcelize

class DialogInputFragment : MaterialDialogFragment<DialogInputFragment, DialogInput, MdfContentInputBinding>() {

    companion object {
        fun create(
            setup: DialogInput,
            dialog: Boolean
        ): DialogInputFragment {
            return DialogInputFragment().apply {
                arguments = MaterialDialogFragmentUtil.createArguments(this, setup, dialog)
            }
        }
    }

    override val wrapInScrollContainer = true

    override fun createContentBinding(
        layoutInflater: LayoutInflater
    ): MdfContentInputBinding {
        return MdfContentInputBinding.inflate(layoutInflater)
    }

    override fun initContentBinding(binding: MdfContentInputBinding, savedInstanceState: Bundle?) {
        val input = state?.input ?: setup.inputInitialValue.getString(requireContext())
        setup.inputDescription.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }
        setup.inputHint.display(binding.mdfTextInputLayout) { view, text ->
            view.hint = text
        }
        binding.mdfTextInputEditText.inputType = setup.inputType
        binding.mdfTextInputEditText.setText(input)
        binding.mdfTextInputEditText.doAfterTextChanged {
            setError("")
        }
        state?.let {
            binding.mdfTextInputEditText.setSelection(it.selectionStart, it.selectionEnd)
        }
    }

    // -------------
    // lifecycle
    // -------------

    private var state: State? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
            state = savedInstanceState.getParcelable(KEY_VIEW_STATE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_VIEW_STATE, State(binding.mdfTextInputEditText))
    }

    // -------------
    // functions
    // -------------

    internal fun getCurrentInput(): String {
        return binding.mdfTextInputEditText.text.toString()
    }

    internal fun setError(error: String) {
        binding.mdfTextInputLayout.error = error.takeIf { it.isNotEmpty() }
    }

    // -------------
    // State
    // -------------

    @Parcelize
    class State(
        val input: String,
        val selectionStart: Int,
        val selectionEnd: Int
    ) : Parcelable {
        constructor(textInputEditText: TextInputEditText): this(
            textInputEditText.text?.toString() ?:"",
            textInputEditText.selectionStart,
            textInputEditText.selectionEnd
        )
    }
}