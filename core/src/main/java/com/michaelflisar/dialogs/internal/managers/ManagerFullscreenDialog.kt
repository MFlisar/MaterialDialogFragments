package com.michaelflisar.dialogs.internal.managers

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.ViewData
import com.michaelflisar.dialogs.core.R

internal class ManagerFullscreenDialog<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, B>, B : ViewBinding>(fragment: F) :
    BaseDialogManager<S, F>(fragment), BaseDialogManager.IView {

    override fun onCreate(savedInstanceState: Bundle?) {
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        fragment.dialog!!.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window!!.setLayout(width, height)
            it.window!!.setWindowAnimations(android.R.style.Animation_Dialog)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mdf_fullscreen_dialog, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): ViewData {

        val containerContent  = view.findViewById<FrameLayout>(R.id.mdf_content)
        val content = fragment.initContentBinding(LayoutInflater.from(fragment.requireContext()))
        val v = MaterialDialogFragmentUtil.createContentView(fragment, content.root)
        containerContent.addView(v)
        fragment.initContentBinding(content, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.mdf_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        toolbar.setNavigationOnClickListener {
            fragment.dismiss()
            fragment.setup.onCancelled()
        }

        val buttonPositive = view.findViewById<Button>(R.id.mdf_button_positive)
        val buttonNegative = view.findViewById<Button>(R.id.mdf_button_negative)
        val buttonNeutral = view.findViewById<Button>(R.id.mdf_button_neutral)

        val viewTitle = ViewData.Title.Toolbar(toolbar)
        val viewButtons = ViewData.Buttons(buttonPositive, buttonNegative, buttonNeutral)

        return ViewData(viewTitle, viewButtons)
    }
}