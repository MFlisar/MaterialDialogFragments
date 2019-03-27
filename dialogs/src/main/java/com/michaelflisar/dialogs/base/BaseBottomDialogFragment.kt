package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.utils.DialogUtil

abstract class BaseBottomDialogFragment : ExtendedFragment(), BaseDialogFragmentHandler.IBaseBottomDialog, DialogFragment {

    private val mHandler: BaseDialogFragmentHandler<BaseBottomDialogFragment>

    // -----------------------------
    // forward functions to handler
    // -----------------------------


    init {
        mHandler = BaseDialogFragmentHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dlg = mHandler.onCreateDialog(savedInstanceState)
        onDialogCreated(dlg)
        return dlg
    }

    protected open fun onDialogCreated(dialog: Dialog) {

    }

    override fun onDestroy() {
        mHandler.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mHandler.onSaveInstanceState(outState)
    }

    // -----------------------------
    // interfaces
    // -----------------------------

    abstract override fun onHandleCreateBottomDialog(savedInstanceState: Bundle?): View

    fun showAllowingStateLoss(activity: FragmentActivity) {
        mHandler.showAllowingStateLoss(activity, this)
    }

    override fun show(activity: FragmentActivity, customSendResultType: SendResultType?, tag: String) {
        mHandler.show(activity, tag, customSendResultType)
    }

    override fun <T : BaseDialogSetup> getSetup(): T = arguments!!.getParcelable("setup")!!

    // -----------------------------
    // Result
    // -----------------------------

    protected fun <X : BaseDialogEvent> sendEvent(event: X) {
        // send result to any custom handler
        DialogSetup.sendResult(event)
        // send result the default way
        DialogUtil.trySendResult(event, this, mHandler.customSendResultType
                ?: DialogSetup.DEFAULT_SEND_RESULT_TYPE)

        onEventSend(event)
    }

    protected open fun <X : BaseDialogEvent> onEventSend(event: X) {

    }
}