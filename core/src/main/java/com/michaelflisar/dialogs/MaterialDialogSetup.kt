package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.text.Text

abstract class MaterialDialogSetup<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, *>> : Parcelable{

    // Key
    abstract val id: Int?

    // Title
    abstract val title: Text

    // Buttons
    abstract val buttonPositive: Text
    abstract val buttonNegative: Text
    abstract val buttonNeutral: Text

    // Behaviour / Style
    abstract val cancelable: Boolean
    abstract val style: DialogStyle
    abstract val swipeDismissable: Boolean
    //abstract val customTheme: Int?

    // Attached Data
    abstract val extras: Bundle?

    // --------------
    // functions
    // --------------

    abstract fun createFragment(showAsDialog: Boolean): F

    fun getButtonText(button: MaterialDialogButton) : Text{
        return when (button) {
            MaterialDialogButton.Positive -> buttonPositive
            MaterialDialogButton.Negative -> buttonNegative
            MaterialDialogButton.Neutral -> buttonNeutral
        }
    }

    fun showDialog(fragmentManager: FragmentManager) {
        val f = createFragment(true)
        f.show(fragmentManager, f::class.java.name)
    }

    fun showDialog(parent: FragmentActivity) {
        showDialog(parent.supportFragmentManager)
    }

    fun showDialog(parent: Fragment) {
        showDialog(parent.childFragmentManager)
    }

    fun show(fragmentManager: FragmentManager) {
        val f = createFragment(false)
        f.show(fragmentManager, f::class.java.name)
    }

    fun show(parent: FragmentActivity) {
        show(parent.supportFragmentManager)
    }

    fun show(parent: Fragment) {
        show(parent.childFragmentManager)
    }

    abstract fun onCancelled()
    abstract fun onButton(fragment: F, button: MaterialDialogButton): Boolean
}