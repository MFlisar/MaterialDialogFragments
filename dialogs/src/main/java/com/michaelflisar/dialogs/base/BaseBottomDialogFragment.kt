package com.michaelflisar.dialogs.base

import android.app.Dialog
import android.os.Bundle
import android.view.View

import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler
import com.michaelflisar.dialogs.utils.DialogUtil

import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity

abstract class BaseBottomDialogFragment : ExtendedFragment(), BaseDialogFragmentHandler.IBaseBottomDialog {
    private val mHandler: BaseDialogFragmentHandler<BaseBottomDialogFragment>

    // -----------------------------
    // forward functions to handler
    // -----------------------------

    val extra: Bundle
        get() = mHandler.extra

    init {
        mHandler = BaseDialogFragmentHandler(EXTRA_KEY, this)
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

    abstract override fun onHandleCreateBottomDialog(savedInstanceState: Bundle): View

    fun createExtra(): Bundle {
        mHandler.createExtra()
        return mHandler.extra
    }

    fun show(activity: FragmentActivity) {
        mHandler.show(activity, this)
    }

    fun showAllowingStateLoss(activity: FragmentActivity) {
        mHandler.showAllowingStateLoss(activity, this)
    }

    fun show(activity: FragmentActivity, tag: String) {
        mHandler.show(activity, tag)
    }

    // -----------------------------
    // Result
    // -----------------------------

    protected fun <X : BaseDialogEvent> sendEvent(event: X) {
        DialogSetup.sendResult(event)
        trySendResultToActivity(event)
    }

    protected fun <X : BaseDialogEvent> trySendResultToActivity(event: X) {
        DialogUtil.trySendResult(event, activity)
    }

    companion object {
        private val EXTRA_KEY = BaseBottomDialogFragment::class.java.name + "|extraData"
    }
}