package com.michaelflisar.dialogs

import android.os.Bundle
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogInfo(
    // Key
    override val id: Int?,
    // Title
    override val title: Text,
    // specific fields
    val text: Text = Text.Empty,
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
) : MaterialDialogSetup<DialogInfo, DialogInfoFragment>() {

    sealed class Event : MaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extras: Bundle?,
            val button: MaterialDialogButton
        ) : Event()

        data class Cancelled(override val id: Int?, override val extras: Bundle?) : Event()
    }

    override fun createFragment(
        showAsDialog: Boolean
    ) = DialogInfoFragment.create(this, showAsDialog)

    // -----------
    // Events
    // -----------

    override fun onCancelled() {
        MaterialDialog.sendEvent(Event.Cancelled(this.id, this.extras))
    }

    override fun onButton(
        dialog: DialogInfoFragment,
        button: MaterialDialogButton
    ) : Boolean {
        MaterialDialog.sendEvent(Event.Result(this.id, this.extras, button))
        return true
    }
}