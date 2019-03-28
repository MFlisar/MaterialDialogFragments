package com.michaelflisar.dialogs.interfaces

import android.os.Bundle
import androidx.fragment.app.ExtendedFragment
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler

open class DialogFragment : ExtendedFragment() {

    companion object {
        const val ARG_SETUP = "setup"
    }

    protected val handler: BaseDialogFragmentHandler<*> = BaseDialogFragmentHandler(this)
    private val internalSetup: BaseDialogSetup by lazy {
        arguments!!.getParcelable<BaseDialogSetup>(ARG_SETUP)!!
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseDialogSetup> getSetup(): T = internalSetup as T

    fun setSetupArgs(setup: BaseDialogSetup) {
        val args = arguments ?: Bundle()
        args.putParcelable(ARG_SETUP, setup)
        arguments = args
    }

    fun show(activity: FragmentActivity, customSendResultType: SendResultType? = DialogSetup.DEFAULT_SEND_RESULT_TYPE, tag: String = this::class.java.name) {
        handler.show(activity, tag, customSendResultType)
    }

    fun showAllowingStateLoss(activity: FragmentActivity) {
        handler.showAllowingStateLoss(activity, this)
    }


}