package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toolbar
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.text.Text

class DialogInfoFragment : MaterialDialogFragment<DialogInfoFragment, DialogInfo, MdfContentInfoBinding>() {

    companion object {
        fun create(
            setup: DialogInfo,
            dialog: Boolean
        ): DialogInfoFragment {
            return DialogInfoFragment().apply {
                arguments = MaterialDialogFragmentUtil.createArguments(this, setup, dialog)
            }
        }
    }

    override val wrapInScrollContainer = true

    override fun createContentBinding(
        layoutInflater: LayoutInflater
    ): MdfContentInfoBinding {
        return MdfContentInfoBinding.inflate(layoutInflater)
    }

    override fun initContentBinding(binding: MdfContentInfoBinding, savedInstanceState: Bundle?) {
        setup.text.display(binding.mdfText)
    }
}