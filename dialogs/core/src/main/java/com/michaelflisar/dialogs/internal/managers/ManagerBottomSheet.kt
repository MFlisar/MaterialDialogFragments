package com.michaelflisar.dialogs.internal.managers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.doOnApplyWindowInsets
import com.google.android.material.bottomsheet.BaseManagerBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelflisar.dialogs.classes.ViewData
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.MaterialDialogFragment
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import kotlin.math.max

internal class ManagerBottomSheet<S : MaterialDialogSetup<S, F>, F: MaterialDialogFragment<F, S, B>, B : ViewBinding>(fragment: F) :
    BaseManagerBottomSheet<S, F, B>(fragment), BaseDialogManager.IView {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mdf_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): ViewData {

        val buttons = view.findViewById<View>(R.id.mdf_buttons)

        val containerContent = view.findViewById<FrameLayout>(R.id.mdf_content)
        val content = fragment.initContentBinding(LayoutInflater.from(fragment.requireContext()))
        val v = MaterialDialogFragmentUtil.createContentView(fragment, content.root)
        containerContent.addView(v)
        fragment.initContentBinding(content, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.mdf_title)

        val buttonPositive = view.findViewById<Button>(R.id.mdf_button_positive)
        val buttonNegative = view.findViewById<Button>(R.id.mdf_button_negative)
        val buttonNeutral = view.findViewById<Button>(R.id.mdf_button_neutral)

        val viewTitle = ViewData.Title.TextView(title)
        val viewButtons = ViewData.Buttons(buttonPositive, buttonNegative, buttonNeutral)

        //view.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
        //    insetView.updatePadding(
        //        bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()).bottom
        //    )
        //}

        // make sure that buttons are always visible and stick to the bottom
        /*getBehaviour()?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                val bottomSheetVisibleHeight = bottomSheet.height - bottomSheet.top
                val translationY =  (bottomSheetVisibleHeight - bottomSheet.height).toFloat()

                buttons.translationY = translationY

                val translation = slideOffset * bottomSheet.height
                //val minTranslation = (bottomSheet.height - buttons.height - (buttons.parent as View).paddingBottom) * -1f
                //buttons.translationY = max(translation, minTranslation)
                //(contentContainer.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = (translation * -1).toInt()
                Log.d("TRANS", "slideOffset = $slideOffset | translationY = $translationY")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })*/

        return ViewData(viewTitle, viewButtons)
    }
}