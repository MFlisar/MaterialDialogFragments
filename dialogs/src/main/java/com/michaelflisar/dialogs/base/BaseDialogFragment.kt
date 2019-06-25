package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.events.DialogCancelledEvent
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.utils.DialogUtil

abstract class BaseDialogFragment : DialogFragment(), BaseDialogFragmentHandler.IBaseDialog {

    // -----------------------------
    // forward functions to handler
    // -----------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return handler.onCreateDialog(savedInstanceState)
    }

    override fun onDestroy() {
        handler.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        handler.onSaveInstanceState(outState)
    }

    // -----------------------------
    // abstract functions
    // -----------------------------

    abstract override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog
}