package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.os.Bundle
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.interfaces.DialogFragment

abstract class BaseDialogFragment<T: BaseDialogSetup> : DialogFragment<T>(), BaseDialogFragmentHandler.IBaseDialog {

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