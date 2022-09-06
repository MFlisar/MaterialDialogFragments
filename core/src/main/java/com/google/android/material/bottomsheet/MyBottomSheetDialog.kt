package com.google.android.material.bottomsheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.*
import com.doOnApplyWindowInsets
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import java.lang.reflect.Field

class MyBottomSheetDialog(
    context: Context,
    theme:Int = 0
): BottomSheetDialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
/*
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)

            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.TRANSPARENT
            it.navigationBarColor = Color.TRANSPARENT
        }

        findViewById<View>(com.google.android.material.R.id.container)?.apply {
            fitsSystemWindows = false
            doOnApplyWindowInsets { insetView, windowInsets, _, initialMargins ->
                insetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(top = initialMargins.top + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
                }
            }
        }

        findViewById<View>(com.google.android.material.R.id.coordinator)?.fitsSystemWindows = false

 */
    }

    //override fun onAttachedToWindow() {
    //    super.onAttachedToWindow()
    //    setDrawBehindStatusBar()
    //}

    /*
    fun setDrawBehindStatusBar() {
        val decorFitsSystemWindow = false
        val sheetView = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!

        window!!.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            WindowCompat.getInsetsController(window, sheetView).let {
                it?.isAppearanceLightNavigationBars = !MaterialDialogFragmentUtil.isDark(sheetView.context)
            }
            WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindow)
        }
        ViewCompat.setOnApplyWindowInsetsListener(sheetView) { view, windowInsets ->
            val currentInsetTypeMask = listOf(
                WindowInsetsCompat.Type.navigationBars()
            ).fold(0) { accumulator, type ->
                accumulator or type
            }
            val insetsContentView = windowInsets.getInsets(currentInsetTypeMask)

            val lp = view.layoutParams as ViewGroup.MarginLayoutParams
            lp.setMargins(0, 0, 0, insetsContentView.bottom)
            view.layoutParams = lp

            //windowInsets
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(sheetView)

    }*/
}