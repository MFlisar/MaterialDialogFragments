package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogPicker<T : Number>(
    // Key
    override val id: Int?,
    // Title
    override val title: Text,
    // specific fields
    val value: T,
    val description: Text = Text.Empty,
    val setup: Setup<T> = Setup.getDefault(value),
    //val pickerStyle: Style = Style.Buttons,
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
) : MaterialDialogSetup<DialogPicker<T>, DialogPickerFragment<T>>() {

    init {
        when (value) {
            is Int,
            is Long,
            is Float,
            is Double -> {
                // OK
            }
            else -> throw RuntimeException("Class ${value::class} not supported!")
        }
    }

    sealed class Event<T> : MaterialDialogEvent {
        data class Result<T : Number>(
            override val id: Int?,
            override val extras: Bundle?,
            val value: T,
            val button: MaterialDialogButton
        ) : Event<T>()

        data class Cancelled<T : Number>(override val id: Int?, override val extras: Bundle?) : Event<T>()
    }

    override fun createFragment(
        showAsDialog: Boolean
    ) = DialogPickerFragment.create(this, showAsDialog)

    // -----------
    // Events
    // -----------

    override fun onCancelled() {
        MaterialDialog.sendEvent(Event.Cancelled<T>(this.id, this.extras))
    }

    override fun onButton(
        fragment: DialogPickerFragment<T>,
        button: MaterialDialogButton
    ): Boolean {
        val input = fragment.getCurrentInput()
        MaterialDialog.sendEvent(Event.Result(this.id, this.extras, input, button))
        return true
    }

    // -----------
    // Functions
    // -----------

    /*
    @Suppress("UNCHECKED_CAST")
    internal fun parse(text: String): T {
        return when (value) {
            is Int -> text.toIntOrNull() ?: 0
            is Long -> text.toLongOrNull() ?: 0L
            is Float -> text.toFloatOrNull() ?: 0f
            is Double -> text.toDoubleOrNull() ?: 0.0
            else -> throw RuntimeException()
        } as T
    }*/

    internal fun adjust(value: T, increase: Boolean) : T {
        val newValue = when (value) {
            is Int -> value as Int + (setup.step as Int * (if (increase) 1 else -1))
            is Long -> value as Long + (setup.step as Long * (if (increase) 1L else -1L))
            is Float -> value as Float + (setup.step as Float * (if (increase) 1f else -1f))
            is Double -> value as Double + (setup.step as Double * (if (increase) 1.0 else -1.0))
            else -> throw RuntimeException()
        } as T

        val tooLow = when (newValue) {
            is Int -> (newValue as Int) < (setup.min as Int)
            is Long -> (newValue as Long) < (setup.min as Long)
            is Float -> (newValue as Float) < (setup.min as Float)
            is Double -> (newValue as Double) < (setup.min as Double)
            else -> throw RuntimeException()
        }

        if (tooLow)
            return setup.min

        val tooHigh = when (newValue) {
            is Int -> (newValue as Int) > (setup.max as Int)
            is Long -> (newValue as Long) > (setup.max as Long)
            is Float -> (newValue as Float) > (setup.max as Float)
            is Double -> (newValue as Double) > (setup.max as Double)
            else -> throw RuntimeException()
        }

        if (tooHigh)
            return setup.max

        return newValue
    }

    // -----------
    // Interfaces/Classes
    // -----------

    //enum class Style {
    //    Buttons,
    //    Seekbar
    //}

    interface Formatter<T>: Parcelable {
        fun format(context: Context, value: T): String
    }

    @Parcelize
    data class Setup<T: Number>(
        val min: T,
        val max: T,
        val step: T,
        val formatter: Formatter<T>? = null
    ) : Parcelable {

        companion object {
            fun<T: Number> getDefault(value: T) : Setup<T> {
                return when (value) {
                    is Int -> Setup(Int.MIN_VALUE, Int.MAX_VALUE, 1)
                    is Long -> Setup(Long.MIN_VALUE, Long.MAX_VALUE, 1L)
                    is Float -> Setup(Float.MIN_VALUE, Float.MAX_VALUE, 1f)
                    is Double -> Setup(Double.MIN_VALUE, Double.MAX_VALUE, 1.0)
                    else -> throw RuntimeException()
                } as Setup<T>
            }
        }
    }
}