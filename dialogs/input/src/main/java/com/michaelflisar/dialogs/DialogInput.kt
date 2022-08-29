package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.input.R
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogInput(
    // Key
    override val id: Int?,
    // Title
    override val title: Text,
    // specific fields
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    val initialValue: Text = Text.Empty,
    val hint: Text = Text.Empty,
    val description: Text = Text.Empty,
    val validator: InputValidator = InputValidatorNone,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Behaviour / Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    override val style: DialogStyle = MaterialDialog.defaults.style,
    override val swipeDismissable: Boolean = MaterialDialog.defaults.swipeDismissable,
    //override val customTheme: Int? = null,
    // Attached Data
    override val extras: Bundle? = null
) : MaterialDialogSetup<DialogInput, DialogInputFragment>() {

    sealed class Event : MaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extras: Bundle?,
            val input: String,
            val button: MaterialDialogButton
        ) : Event()

        data class Cancelled(override val id: Int?, override val extras: Bundle?) : Event()
    }

    override fun createFragment(
        showAsDialog: Boolean
    ) = DialogInputFragment.create(this, showAsDialog)

    // -----------
    // Events
    // -----------

    override fun onCancelled() {
        MaterialDialog.sendEvent(Event.Cancelled(this.id, this.extras))
    }

    override fun onButton(
        fragment: DialogInputFragment,
        button: MaterialDialogButton
    ) : Boolean {
        val input = fragment.getCurrentInput()
        return if (fragment.setup.validator.isValid(input)) {
            MaterialDialog.sendEvent(Event.Result(this.id, this.extras, input, button))
            true
        } else {
            fragment.setError(fragment.setup.validator.getError(fragment.requireContext(), input))
            false
        }
    }

    // -----------
    // Interfaces/Classes
    // -----------

    interface InputValidator : Parcelable {
        fun isValid(input: String): Boolean
        fun getError(context: Context, input: String): String
    }

    @Parcelize
    object InputValidatorNone: InputValidator {
        override fun isValid(input: String) = true
        override fun getError(context: Context, input: String) = ""
    }

    @Parcelize
    object InputValidatorNotEmpty: InputValidator {
        override fun isValid(input: String) = input.isNotEmpty()
        override fun getError(context: Context, input: String) = context.getString(R.string.mdf_error_only_non_empty_inputs_allowed)
    }
}