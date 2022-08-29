package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.internal.managers.BaseDialogManager
import com.michaelflisar.dialogs.internal.managers.ManagerBottomSheet
import com.michaelflisar.dialogs.internal.managers.ManagerDialog
import com.michaelflisar.dialogs.internal.managers.ManagerFullscreenDialog


abstract class MaterialDialogFragment<F: MaterialDialogFragment<F, S, B>, S : MaterialDialogSetup<S, F>, B : ViewBinding>
    : AppCompatDialogFragment() {

    companion object {
        const val KEY_VIEW_STATE = "VIEW_STATE"
    }

    lateinit var binding: B

    // ------------------
    // abstract functions
    // ------------------

    abstract val wrapInScrollContainer: Boolean
    protected abstract fun createContentBinding(layoutInflater: LayoutInflater): B
    abstract fun initContentBinding(binding: B, savedInstanceState: Bundle?)

    fun initContentBinding(layoutInflater: LayoutInflater): B {
        binding = createContentBinding(layoutInflater)
        return binding
    }

    // ------------------
    // variables
    // ------------------

    val setup: S by lazy {
        MaterialDialogFragmentUtil.getSetup(this)
    }

    val showAsDialog: Boolean by lazy {
        MaterialDialogFragmentUtil.getShowAsDialog(this)
    }


    private lateinit var dialogManager: BaseDialogManager<S, F>

    // ------------------
    // dialog
    // ------------------

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (dialogManager is BaseDialogManager.IDialog) {
            (dialogManager as BaseDialogManager.IDialog).onCreateDialog(savedInstanceState)
        } else super.onCreateDialog(savedInstanceState)
    }

    // ------------------
    // view
    // ------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (dialogManager is BaseDialogManager.IView) {
            (dialogManager as BaseDialogManager.IView).onCreateView(
                inflater,
                container,
                savedInstanceState
            )
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (dialogManager is BaseDialogManager.IView) {
            val viewData =
                (dialogManager as BaseDialogManager.IView).onViewCreated(view, savedInstanceState)
            viewData.init(this as F, setup)
        } else {
            super.onViewCreated(view, savedInstanceState)
        }
    }

    // ------------------
    // fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogManager = when (setup.style) {
            is DialogStyle.BottomSheet -> ManagerBottomSheet(this as F)
            DialogStyle.Dialog -> ManagerDialog(this as F)
            DialogStyle.FullScreen -> ManagerFullscreenDialog(this as F)
        }
        dialogManager.onCreate(savedInstanceState)

        // if the user wants a simple dialog behaviour, we do NOT restore this fragment and simply dismiss it immediately
        if (savedInstanceState != null && showAsDialog)
            dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialogManager.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dialogManager.onSaveInstanceState(outState)
    }

    override fun dismiss() {
        (dialogManager as? BaseDialogManager.ICustomDismiss)?.dismiss() ?: super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        (dialogManager as? BaseDialogManager.ICustomDismiss)?.dismissAllowingStateLoss()
            ?: super.dismissAllowingStateLoss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setup.onCancelled()
    }

    // ------------------
    // internal calls to super functions
    // ------------------

    internal fun superDismiss() {
        super.dismiss()
    }

    internal fun superDismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
    }
}