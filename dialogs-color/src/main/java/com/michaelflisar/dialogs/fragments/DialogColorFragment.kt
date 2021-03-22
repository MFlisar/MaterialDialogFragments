package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Outline
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.utils.MDUtil
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.adapter.ColorAdapter
import com.michaelflisar.dialogs.adapter.MainColorAdapter
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.DialogColorBinding
import com.michaelflisar.dialogs.events.DialogColorEvent
import com.michaelflisar.dialogs.setups.DialogColor
import com.michaelflisar.dialogs.utils.ColorUtil
import com.michaelflisar.dialogs.utils.RecyclerViewUtil
import kotlin.math.roundToInt


class DialogColorFragment : MaterialDialogFragment<DialogColor>() {

    companion object {
        fun create(setup: DialogColor): DialogColorFragment {
            val dlg = DialogColorFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    internal lateinit var binding: DialogColorBinding
    internal lateinit var mainColorAdapter: MainColorAdapter
    internal lateinit var colorAdapter: ColorAdapter

    private var selectedPage: Int = 0
    private var selectedColorPickerGroup: GroupedColor = ColorDefinitions.COLORS[0]
    private var selectedColorPickerColor: Int? = null
    private var selectedColorPickerTransparency: Int = 255
    private var selectedCustomColor: Int? = null

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) {
            selectedPage = savedInstanceState.getInt("selectedPage")
            selectedColorPickerGroup = ColorDefinitions.COLORS[savedInstanceState.getInt("selectedColorPickerGroup")]
            selectedColorPickerColor = "selectedColorPickerColor".let { if (savedInstanceState.containsKey(it)) savedInstanceState.getInt(it) else null }
            selectedColorPickerTransparency = savedInstanceState.getInt("selectedColorPickerTransparency")
            selectedCustomColor = "selectedCustomColor".let { if (savedInstanceState.containsKey(it)) savedInstanceState.getInt(it) else null }
        } else {
            selectedColorPickerGroup = ColorUtil.getNearestColorGroup(requireActivity(), setup.color)
            selectedColorPickerColor = selectedColorPickerGroup.findMatchingColor(requireContext(), setup.color)
            selectedColorPickerTransparency = Color.alpha(setup.color)
            selectedCustomColor = setup.color
        }

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(requireActivity(), this, false)

        dialog.customView(
                R.layout.dialog_color,
                scrollable = false,
                noVerticalPadding = true
        )
                .positiveButton(R.string.color_dialog_select) {
                    val selectedColor = getSelectedColor()
                    if (selectedColor == null) {
                        Toast.makeText(requireActivity(), R.string.color_dialog_nothing_selected, Toast.LENGTH_SHORT).show()
                        return@positiveButton
                    }
                    sendEvent(DialogColorEvent(setup, WhichButton.POSITIVE.ordinal, DialogColorEvent.Data(selectedColor)))
                    dismiss()
                }
                .noAutoDismiss()
                .negativeButton(setup) {
                    sendEvent(DialogColorEvent(setup, WhichButton.NEGATIVE.ordinal, null))
                    dismiss()
                }
                .neutralButton(setup) {
                    sendEvent(DialogColorEvent(setup, WhichButton.NEUTRAL.ordinal, null))
                }

        val view = dialog.getCustomView()
        binding = DialogColorBinding.bind(view)
        binding.toolbar.title = setup.title.get(requireActivity())
        binding.toolbar.menu?.apply {
            add(R.string.color_dialog_toggle_view)
            getItem(0).apply {
                setIcon(R.drawable.ic_baseline_sync_alt_24)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon?.setColorFilter(if (DialogSetup.isUsingDarkTheme(view.context)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            }
        }
        binding.toolbar.setOnMenuItemClickListener {
            binding.pager.currentItem = (binding.pager.currentItem + 1) % 2
            true
        }

        // 1) init ViewPager
        initViewPager()

        // 2) init adapter for picker page
        initPickerPage()

        // 3) init custom color page
        initCustomPage()

        // 4) update dependencies
        updateSelectedColorDependencies(dialog)

        return dialog
    }

    private fun updateSelectedColorDependencies(dlg: MaterialDialog?, pickerAlphaChanged: Boolean = false) {
        val selectedColor = getSelectedColor()
                ?: selectedColorPickerGroup.getMainColor(requireContext())
        val textColor = ColorUtil.getBestTextColor(selectedColor)

//        binding.tabs.setBackgroundColor(selectedColor)
//        binding.tabs.tabTextColors = ColorStateList.valueOf(textColor)
//        binding.tabs.setSelectedTabIndicatorColor(textColor)

        binding.tvTransparancy.text = "${(100 * selectedColorPickerTransparency / 255f).roundToInt()}%"
        if (pickerAlphaChanged) {
            colorAdapter.setTransparency(selectedColorPickerTransparency)
        }

        // TODO: Problem: this only works for dialogs, not for bottom sheets!
        (dlg ?: dialog as MaterialDialog).onShow {
            val radius = it.cornerRadius ?: MDUtil.resolveDimen(requireContext(), attr = R.attr.md_corner_radius) {
                resources.getDimension(R.dimen.md_dialog_default_corner_radius)
            }
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                it.view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, radius)
                    }
                }
                it.view.clipChildren = true
                it.view.clipToOutline = true
            }
            it.getActionButton(WhichButton.POSITIVE).apply {
                setBackgroundColor(selectedColor)
                setTextColor(textColor)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedColorPickerGroup", ColorDefinitions.COLORS.indexOf(selectedColorPickerGroup))
        outState.putInt("selectedPage", selectedPage)
        selectedColorPickerColor?.let { outState.putInt("selectedColorPickerColor", it) }
        outState.putInt("selectedColorPickerTransparency", selectedColorPickerTransparency)
        selectedCustomColor?.let { outState.putInt("selectedCustomColor", it) }
    }

    // -----------------
    // private helper functions
    // -----------------

    private fun initViewPager() {
        val adapter = ColorPageAdapter(
                listOf(binding.page1, binding.page2),
                listOf(R.string.color_dialog_presets, R.string.color_dialog_custom)

        )
        binding.pager.adapter = adapter
        binding.pager.offscreenPageLimit = 5
//        binding.tabs.setupWithViewPager(binding.pager)
        binding.dots.attachViewPager(binding.pager)
        binding.pager.setCurrentItem(selectedPage, false)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                selectedPage = position
                updateSelectedColorDependencies(null, false)
                if (selectedPage == 0) {
                    ensureMainColorIsVisible()
                }
            }
        })

        binding.pager.wrapContent = !isLandscape()
    }

    private fun initPickerPage() {

        val isLandscape = isLandscape()

        // 1) RecyclerViews
        colorAdapter = ColorAdapter(isLandscape, selectedColorPickerGroup, selectedColorPickerTransparency, selectedColorPickerGroup.findMatchingColorIndex(requireContext(), selectedColorPickerColor)) { adapter, item, color, pos ->
            selectedColorPickerColor = color
            colorAdapter.updateSelection(pos)
            updateSelectedColorDependencies(null)
            if (setup.moveToCustomPageOnPickerSelection || setup.updateCustomColorOnPickerSelection) {
                selectedCustomColor = selectedColorPickerColor
                binding.colorPicker.setCurrentColor(selectedCustomColor!!)
                if (setup.moveToCustomPageOnPickerSelection) {
                    binding.pager.setCurrentItem(1, true)
                }
            }
        }
        mainColorAdapter = MainColorAdapter(ColorDefinitions.COLORS, getSelectedGroupIndex()) { adapter, item, color, pos ->
            // nicht hier speichern, das macht den Dialog etwas langsam weil das schreiben die UI blockiert
            // => nur wert updaten und später speichern
            if (color != selectedColorPickerGroup) {
                selectedColorPickerGroup = color
                selectedColorPickerColor = null
                mainColorAdapter.update(pos)
                colorAdapter.updateGroupColor(selectedColorPickerGroup, true)
                binding.rvMaterialColors.scrollToPosition(0)
                binding.tvGroupColorHeader.text = color.getHeaderDescription(requireActivity())
                updateSelectedColorDependencies(null)
            }
        }

        binding.tvGroupColorHeader.text = selectedColorPickerGroup.getHeaderDescription(requireActivity())

        val columns = if (isLandscape) 7 else 4
        binding.rvMaterialColors.layoutManager = GridLayoutManager(activity, columns, RecyclerView.VERTICAL, false)
        binding.rvMaterialColors.adapter = colorAdapter
        binding.rvMaterialMainColors.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvMaterialMainColors.adapter = mainColorAdapter

        ensureMainColorIsVisible()

        // 2) Slider
        if (!setup.showAlpha) {
            binding.tvTitleTransparancy.visibility = View.GONE
            binding.llTransparancy.visibility = View.GONE
        } else {
            binding.tvTitleTransparancy.visibility = View.VISIBLE
            binding.llTransparancy.visibility = View.VISIBLE
            binding.slTransparancy.value = selectedColorPickerTransparency / 255f
            binding.slTransparancy.setLabelFormatter {
                "${(100 * it).roundToInt()}%"
            }
            binding.slTransparancy.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) {
                    selectedColorPickerTransparency = (255f * value).roundToInt()
                    selectedColorPickerColor?.let {
                        selectedColorPickerColor = ColorUtil.adjustAlpha(it, selectedColorPickerTransparency)
                    }
                    updateSelectedColorDependencies(null, true)
                }
            }
        }

        // 3) Group Header
        binding.tvGroupColorHeader.visibility = if (isLandscape()) View.GONE else View.VISIBLE
    }

    private fun initCustomPage() {
        binding.colorPicker.color = setup.color
        binding.colorPicker.showAlpha(setup.showAlpha)
        binding.colorPicker.addColorObserver { observableColor ->
            selectedCustomColor = observableColor.color
            updateSelectedColorDependencies(null)
        }
    }

    private fun getSelectedGroupIndex() = ColorDefinitions.COLORS.indexOf(selectedColorPickerGroup)

    private fun getSelectedColor(): Int? {
        return if (selectedPage == 0) {
            selectedColorPickerColor
        } else {
            selectedCustomColor
        }
    }

    private fun ensureMainColorIsVisible() {
        val scrollToMainColor = {
            if (!RecyclerViewUtil.isViewVisible(binding.rvMaterialMainColors, getSelectedGroupIndex()))
                binding.rvMaterialMainColors.scrollToPosition(getSelectedGroupIndex())
        }

        binding.rvMaterialColors.post {
            scrollToMainColor()
        }

//        if (binding.rvMaterialColors.isLaidOut) {
//            scrollToMainColor()
//        } else {
//            binding.rvMaterialColors.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//                        binding.rvMaterialColors.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    else {
//                        @Suppress("DEPRECATION")
//                        binding.rvMaterialColors.viewTreeObserver.removeGlobalOnLayoutListener(this)
//                    }
//                    scrollToMainColor()
//                }
//            })
//        }
    }

    // -----------------
    // Pager Adapter
    // -----------------

    internal inner class ColorPageAdapter(
            private val views: List<View>,
            private val titles: List<Int>
    ) : PagerAdapter() {

        override fun instantiateItem(collection: View, position: Int): Any {
            return views[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun getCount(): Int {
            return views.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position].takeIf { it != -1 }?.let { getString(it) }
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1 as View
        }
    }

}
