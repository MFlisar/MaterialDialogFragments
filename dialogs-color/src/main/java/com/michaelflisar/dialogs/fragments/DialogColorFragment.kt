package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayout
import com.michaelflisar.dialogs.adapter.ColorAdapter
import com.michaelflisar.dialogs.adapter.MainColorAdapter
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.events.DialogColorEvent
import com.michaelflisar.dialogs.negativeButton
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.setups.DialogColor
import com.michaelflisar.dialogs.utils.ColorUtil
import com.michaelflisar.dialogs.utils.RecyclerViewUtil
import com.rarepebble.colorpicker.ColorPickerView

class DialogColorFragment : BaseDialogFragment<DialogColor>() {

    companion object {

        fun create(setup: DialogColor): DialogColorFragment {
            val dlg = DialogColorFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    internal lateinit var pageOne: View
    internal lateinit var pageTwo: View
    internal lateinit var tvPageTwoHeader: TextView
    internal lateinit var tabs: TabLayout
    internal lateinit var pager: ViewPager

    internal lateinit var colorPicker: ColorPickerView

    internal lateinit var rvMaterialMainColors: RecyclerView
    internal lateinit var rvMaterialColors: RecyclerView

    private var selectedColorGroupIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null)
            selectedColorGroupIndex = savedInstanceState.getInt("selectedColorGroupIndex")
        else {
            selectedColorGroupIndex = ColorUtil.getNearestColorGroup(activity!!, setup.color)
        }

        val dialog = MaterialDialog(activity!!)
                .customView(
                        R.layout.dialog_color,
                        scrollable = false,
                        noVerticalPadding = true
                )
                .positiveButton(R.string.dialogs_save) {
                    val c = colorPicker.color
                    sendEvent(DialogColorEvent(setup, WhichButton.POSITIVE.ordinal, DialogColorEvent.Data(selectedColorGroupIndex, c)))
                    dismiss()
                }
                .cancelable(true)
                .noAutoDismiss()

        setup.negButton?.let {
            dialog.negativeButton(it) {
                sendEvent(DialogColorEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                dismiss()
            }
        }

        setup.neutrButton?.let {
            dialog.neutralButton(it) {
                sendEvent(DialogColorEvent(setup, WhichButton.NEUTRAL.ordinal, null))
            }
        }

        val view = dialog.getCustomView()

        pageOne = view.findViewById(R.id.pageOne)
        pageTwo = view.findViewById(R.id.pageTwo)
        tvPageTwoHeader = view.findViewById(R.id.tvPageTwoHeader)

        tabs = view.findViewById(R.id.tabs)
        pager = view.findViewById(R.id.pager)
        colorPicker = view.findViewById(R.id.colorPicker)
        rvMaterialMainColors = view.findViewById(R.id.rvMaterialMainColors)
        rvMaterialColors = view.findViewById(R.id.rvMaterialColors)

        updateTitle(setup.color)

        val colorAdapter = ColorAdapter(ColorUtil.COLORS[selectedColorGroupIndex], ColorAdapter.IColorClickedListener { _, _, c, _ ->
            colorPicker.setCurrentColor(c)
            pager.setCurrentItem(1, true)
        })

        tvPageTwoHeader.text = ColorUtil.COLORS[selectedColorGroupIndex].getHeaderDescription(activity)
        val mainColorAdapter = MainColorAdapter(setup.useDarkTheme(), ColorUtil.COLORS, selectedColorGroupIndex, MainColorAdapter.IMainColorClickedListener { adapter, _, c, pos ->
            // nicht hier speichern, das macht den Dialog etwas langsam weil das schreiben die UI blockiert
            // => nur wert updaten und später speichern
            selectedColorGroupIndex = pos

            adapter.setSelected(pos)
            colorAdapter.setGroupColor(c)
            rvMaterialColors.scrollToPosition(0)
            tvPageTwoHeader.text = c.getHeaderDescription(activity)
        })

        rvMaterialColors.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvMaterialColors.adapter = colorAdapter

        rvMaterialMainColors.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvMaterialMainColors.adapter = mainColorAdapter
        rvMaterialColors.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    rvMaterialColors.viewTreeObserver.removeOnGlobalLayoutListener(this)
                else {
                    @Suppress("DEPRECATION")
                    rvMaterialColors.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                if (!RecyclerViewUtil.isViewVisible(rvMaterialMainColors, selectedColorGroupIndex))
                    rvMaterialMainColors.scrollToPosition(selectedColorGroupIndex)
            }
        })

        val adapter = ColorPageAdapter()
        pager.adapter = adapter
        pager.offscreenPageLimit = 3
        tabs.setupWithViewPager(pager)

        colorPicker.color = setup.color
        colorPicker.showAlpha(setup.showAlpha)
        colorPicker.addColorObserver { observableColor -> updateTitle(observableColor.color) }

        return dialog
    }

    private fun updateTitle(color: Int) {
        tabs.setBackgroundColor(color)
        tabs.tabTextColors = ColorStateList.valueOf(ColorUtil.getBestTextColor(color))
        tabs.setSelectedTabIndicatorColor(ColorUtil.getBestTextColor(color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedColorGroupIndex", selectedColorGroupIndex)
    }

    internal inner class ColorPageAdapter : PagerAdapter() {

        override fun instantiateItem(collection: View, position: Int): Any {

            when (position) {
                0 -> return pageOne
                1 -> return pageTwo
                else -> throw RuntimeException("Position not handled!")
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return getString(R.string.material_color)
                1 -> return getString(R.string.settings_color)
            }
            return null
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1 as View
        }
    }

}
