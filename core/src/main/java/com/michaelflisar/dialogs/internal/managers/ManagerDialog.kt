package com.michaelflisar.dialogs.internal.managers

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.core.R

internal class ManagerDialog<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, B>, B : ViewBinding>(fragment: F) :
    BaseDialogManager<S, F>(fragment), BaseDialogManager.IDialog {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val content = fragment.initContentBinding(LayoutInflater.from(fragment.requireContext()))
        val builder = MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(fragment.setup.title.get(fragment.requireContext()))
        //.setMessage(fragment.setup.text.get(fragment.requireContext()))
        val v = createContentView(content)
        v.id = R.id.message
        builder.setView(v)
        initButtons(builder)
        val dlg = builder.create()
        initButtonClickListeners(dlg)
        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dlg.requestWindowFeature(Window.FL)
        //dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        //dlg.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        fragment.initContentBinding(content, savedInstanceState)

        return dlg
    }

    private fun createContentView(content: B): FrameLayout {
        val frameLayout = FrameLayout(fragment.requireContext())
        val frameChild = if (fragment.wrapInScrollContainer) {
            val scrollView = ScrollView(fragment.requireContext())
            scrollView.addView(
                content.root,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            scrollView
        } else {
            content.root
        }
        frameLayout.addView(
            frameChild,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val dp = MaterialDialogFragmentUtil.dpToPx(1)
                topMargin = dp * 16
                marginStart = dp * 24
                marginEnd = dp * 24
            })
        return frameLayout
    }

    private fun initButtons(builder: MaterialAlertDialogBuilder) {
        // if we set button click listeners here, we cant control if the dialog gets dismissed or not!
        fragment.setup.buttonPositive.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setPositiveButton(it, null)
            }
        fragment.setup.buttonNegative.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setNegativeButton(it, null)
            }
        fragment.setup.buttonNeutral.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setNeutralButton(it, null)
            }
    }

    private fun initButtonClickListeners(dialog: AlertDialog) {
        dialog.setOnShowListener {
            fragment.setup.buttonPositive.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
                ?.let {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (fragment.setup.onButton(fragment, MaterialDialogButton.Positive))
                            fragment.dismiss()
                    }
                }
            fragment.setup.buttonNegative.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
                ?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                        if (fragment.setup.onButton(fragment, MaterialDialogButton.Negative))
                            fragment.dismiss()
                    }
                }
            fragment.setup.buttonNeutral.get(fragment.requireContext()).takeIf { it.isNotEmpty() }
                ?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        if (fragment.setup.onButton(fragment, MaterialDialogButton.Neutral))
                            fragment.dismiss()
                    }
                }
        }
    }

    //var slidrInterface: SlidrInterface? = null
//
    //override fun onResume(owner: LifecycleOwner) {
    //    super.onResume(owner)
    //    if (slidrInterface == null)
    //        slidrInterface = Slidr.replace(
    //            fragment.dialog!!.findViewById(R.id.message),
    //            SlidrConfig.Builder().position(SlidrPosition.LEFT).build()
    //        );
    //}
}