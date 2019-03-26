package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.utils.DialogUtil

abstract class BaseDialogFragment : ExtendedFragment(), BaseDialogFragmentHandler.IBaseDialog, DialogFragment {
    private val mHandler: BaseDialogFragmentHandler<BaseDialogFragment>

    // -----------------------------
    // forward functions to handler
    // -----------------------------

    val extra: Bundle?
        get() = mHandler.extra

    init {
        mHandler = BaseDialogFragmentHandler(EXTRA_KEY, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return mHandler.onCreateDialog(savedInstanceState)
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

    abstract override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog

    fun createExtra(): Bundle {
        mHandler.createExtra()
        return mHandler.extra
    }

    fun showAllowingStateLoss(activity: FragmentActivity) {
        mHandler.showAllowingStateLoss(activity, this)
    }

    override fun show(activity: FragmentActivity, customSendResultType: SendResultType?, tag: String) {
        mHandler.show(activity, tag, customSendResultType)
    }

    // -----------------------------
    // Result
    // -----------------------------

    protected fun <X : BaseDialogEvent> sendEvent(event: X) {
        // send result to any custom handler
        DialogSetup.sendResult(event)
        // send result the default way
        DialogUtil.trySendResult(event, this, mHandler.customSendResultType ?: DialogSetup.DEFAULT_SEND_RESULT_TYPE)
    }

    companion object {
        private val EXTRA_KEY = BaseDialogFragment::class.java.name + "|extraData"
    }
}