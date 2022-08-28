package com.michaelflisar.dialogs.classes

import android.view.View
import android.widget.Button
import android.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.text.Text

class ViewData(
    val title: Title,
    val buttons: Buttons
) {

    fun <S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, *>> init(
        fragment: F,
        setup: S
    ) {

        // Title
        this.title.setText(setup.title)

        // Buttons
        initButton(fragment, setup, MaterialDialogButton.Positive)
        initButton(fragment, setup, MaterialDialogButton.Negative)
        initButton(fragment, setup, MaterialDialogButton.Neutral)

    }

    private fun <S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, *>> initButton(
        fragment: F,
        setup: S,
        buttonType: MaterialDialogButton,
    ) {
        val buttonText = setup.getButtonText(buttonType)
        val button = buttons.getButton(buttonType)
        if (buttonText.isEmpty(fragment.requireContext())) {
            button.visibility = View.GONE
        } else {
            buttonText.display(button) { view, text ->
                view.text = text
            }
            button.setOnClickListener {
                if (setup.onButton(fragment, buttonType))
                fragment.dismiss()
            }
        }
    }

    class Buttons(
        val buttonPositive: Button,
        val buttonNegative: Button,
        val buttonNeutral: Button
    ) {

        fun getButton(buttonType: MaterialDialogButton): Button {
            return when (buttonType) {
                MaterialDialogButton.Positive -> buttonPositive
                MaterialDialogButton.Negative -> buttonNegative
                MaterialDialogButton.Neutral -> buttonNeutral
            }
        }
    }

    sealed class Title {

        abstract fun setText(text: Text)

        internal class Toolbar(val toolbar: MaterialToolbar) : Title() {
            override fun setText(text: Text) {
                text.display(toolbar) { view, text ->
                    view.title = text
                }
            }
        }

        internal class TextView(val textView: android.widget.TextView) : Title() {
            override fun setText(text: Text) {
                text.display(textView)
            }
        }
    }
}