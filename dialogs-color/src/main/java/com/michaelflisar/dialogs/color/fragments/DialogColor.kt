package com.michaelflisar.dialogs.color.fragments

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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayout
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.adapter.ColorAdapter
import com.michaelflisar.dialogs.color.adapter.MainColorAdapter
import com.michaelflisar.dialogs.color.utils.ColorUtil
import com.michaelflisar.dialogs.color.utils.RecyclerViewUtil
import com.michaelflisar.dialogs.events.BaseDialogEvent
import com.rarepebble.colorpicker.ColorPickerView

class DialogColor : BaseDialogFragment() {

    internal lateinit var pageOne: View
    internal lateinit var pageTwo: View
    internal lateinit var tvPageTwoHeader: TextView
    internal lateinit var tabs: TabLayout
    internal lateinit var pager: ViewPager

    internal lateinit var colorPicker: ColorPickerView

    internal lateinit var rvMaterialMainColors: RecyclerView
    internal lateinit var rvMaterialColors: RecyclerView

    private var mSelectedColorGroupIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id = arguments!!.getInt("id")
        val darkTheme = arguments!!.getBoolean("darkTheme")
        if (savedInstanceState != null)
            mSelectedColorGroupIndex = savedInstanceState.getInt("mSelectedColorGroupIndex")
        else {
            mSelectedColorGroupIndex = 0
            if (arguments!!.containsKey("selectedColorGroupIndex")) {
                val selectedColorGroupIndex = arguments!!.getInt("selectedColorGroupIndex")
                if (selectedColorGroupIndex >= 0 && selectedColorGroupIndex < ColorUtil.COLORS.size)
                    mSelectedColorGroupIndex = selectedColorGroupIndex
            }
        }

        val color = arguments!!.getInt("color")

        val dialog = MaterialDialog(activity!!)
                .customView(R.layout.dialog_color, scrollable = false)
                .positiveButton(R.string.dialogs_save) {
                    val c = colorPicker.color
                    sendEvent(DialogColorEvent(id, mSelectedColorGroupIndex, c))
                    dismiss()
                }
                .cancelable(true)
                .noAutoDismiss()

        val view = dialog.getCustomView()

        pageOne = view.findViewById(R.id.pageOne)
        pageTwo = view.findViewById(R.id.pageTwo)
        tvPageTwoHeader = view.findViewById(R.id.tvPageTwoHeader)

        tabs = view.findViewById(R.id.tabs)
        pager = view.findViewById(R.id.pager)
        colorPicker = view.findViewById(R.id.colorPicker)
        rvMaterialMainColors = view.findViewById(R.id.rvMaterialMainColors)
        rvMaterialColors = view.findViewById(R.id.rvMaterialColors)

        updateTitle(color)

        val colorAdapter = ColorAdapter(ColorUtil.COLORS[mSelectedColorGroupIndex], ColorAdapter.IColorClickedListener { _, _, c, _ ->
            colorPicker.setCurrentColor(c)
            pager.setCurrentItem(1, true)
        })

        tvPageTwoHeader.text = ColorUtil.COLORS[mSelectedColorGroupIndex].getHeaderDescription(activity)
        val mainColorAdapter = MainColorAdapter(darkTheme, ColorUtil.COLORS, mSelectedColorGroupIndex, MainColorAdapter.IMainColorClickedListener { adapter, _, c, pos ->
            // nicht hier speichern, das macht den Dialog etwas langsam weil das schreiben die UI blockiert
            // => nur wert updaten und später speichern
            mSelectedColorGroupIndex = pos

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
                if (!RecyclerViewUtil.isViewVisible(rvMaterialMainColors, mSelectedColorGroupIndex))
                    rvMaterialMainColors.scrollToPosition(mSelectedColorGroupIndex)
            }
        })

        val adapter = ColorPageAdapter()
        pager.adapter = adapter
        pager.offscreenPageLimit = 3
        tabs.setupWithViewPager(pager)

        colorPicker.color = color
        colorPicker.showAlpha(false)
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
        outState.putInt("mSelectedColorGroupIndex", mSelectedColorGroupIndex)
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

    class DialogColorEvent(id: Int, var colorGroupIndex: Int, var color: Int) : BaseDialogEvent(null, id)

    companion object {
        fun create(id: Int, darkTheme: Boolean, selectedColorGroupIndex: Int?, color: Int): DialogColor {
            val dlg = DialogColor()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putBoolean("darkTheme", darkTheme)
            if (selectedColorGroupIndex != null)
                bundle.putInt("selectedColorGroupIndex", selectedColorGroupIndex)
            bundle.putInt("color", color)
            dlg.arguments = bundle
            return dlg
        }
    }
}
