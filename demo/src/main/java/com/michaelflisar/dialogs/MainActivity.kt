package com.michaelflisar.dialogs

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ActivityMainBinding
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.LongToast
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.text.Text
import com.michaelflisar.text.toText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.onMaterialDialogEvent
import kotlinx.parcelize.Parcelize


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) init views
        initViews(savedInstanceState)

        // 2) init adapter with demo items and add items
        val itemAdapter = initRecyclerView()
        addInfoDialogItems(itemAdapter)
        addInputDialogItems(itemAdapter)
        //addListDialogItems(itemAdapter)
        //addNumberDialogItems(itemAdapter)
        //addProgressDialogItems(itemAdapter)
        //addFastAdapterDialogItems(itemAdapter)
        //addColorDialogItems(itemAdapter)
        //addDateTimeDialogItems(itemAdapter)
        //addFrequencyDialogItems(itemAdapter)
        //addDebugDialogItems(itemAdapter)
        //addAdsDialogItems(itemAdapter)

        // 3) listen to dialog events
        addListeners()
    }



    private fun addListeners() {
        // listen to ALL events - listener will always be called for events that are of the provided class OR any sub class
        //onMaterialDialogEvent<MaterialDialogEvent> { event ->
        //    // ...
        //}

        onMaterialDialogEvent<DialogInfo.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogInput.Event> { event ->
            showToast(event)
        }
    }

    // -------------------
    // demo activity
    // -------------------

    private fun initViews(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getParcelable<State>("viewState")

        state?.let {
            binding.mbTheme.setSingleSelection(it.theme)
            binding.mbStyle.setSingleSelection(it.style)
            binding.cbShowAsDialogFragment.isChecked = it.showAsDialogFragment
        }

        binding.mbTheme.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btThemeAuto -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    R.id.btThemeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    R.id.btThemeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putParcelable("viewState", State(
            binding.mbTheme.checkedButtonId,
            binding.mbStyle.checkedButtonId,
            binding.cbShowAsDialogFragment.isChecked
        ))
    }

    // -------------------
    // helper functions for content setup
    // -------------------

    private fun getStyleFromCheckbox(): DialogStyle {
        return when (binding.mbStyle.checkedButtonId) {
            R.id.btStyleDialog -> DialogStyle.Dialog
            R.id.btStyleBottomSheet -> DialogStyle.BottomSheet()
            R.id.btStyleFullscreen -> DialogStyle.FullScreen
            else -> throw RuntimeException()
        }
    }

    private fun initRecyclerView(): ItemAdapter<IItem<*>> {
        val itemAdapter = ItemAdapter<IItem<*>>()
        val adapter = FastAdapter.with(itemAdapter)

        val selectExtension = adapter.getSelectExtension()
        selectExtension.isSelectable = true

        adapter.onClickListener = { _, _, item, _ ->
            if (item is DemoItem) {
                item.function()
            }
            true
        }
        binding.rvDemoItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvDemoItems.adapter = adapter
        return itemAdapter
    }

    private fun addInfoDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("INFO demos"),
            DemoItem("Short info", "Show a simple short info dialog") {
                DialogInfo(
                    id = 10,
                    title = "Info Title".toText(),
                    text = "Some info text...".toText(),
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem("Short info", "Show a simple short info dialog with a spannable text") {
                DialogInfo(
                    id = 11,
                    title = "Info Title".toText(),
                    text = SpannableString("Here is some important information:\n\nSpannable strings are supported as well!").also {
                        it.setSpan(ForegroundColorSpan(Color.RED), 13, 22, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }.toText(),
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem("Long info", "Show a simple long info dialog") {
                DialogInfo(
                    id = 12,
                    title = "Info Title".toText(),
                    text = "Text... ".repeat(500).toText(),
                    style = getStyleFromCheckbox(),
                    buttonNegative = "Return".toText()
                )
                    .showInCorrectMode(this)
            },
            //DemoItem(
            //    "Info demo with timeout",
            //    "Show a info dialog with an ok button that will only be enabled after 10s and which shows a colored warning message"
            //) {
            //    DialogInfo(
            //        11,
            //        "Info Title".toText(),
            //        "Some info about a dangerous action + 10s timeout until the ok button can be clicked.\nRotate me and I'll remember the already past by time.".toText(),
            //        warning = "Attention: Dangerous action!".toText(),
            //        warningSeparator = "\n\n",
            //        cancelable = false,
            //        timerPosButton = 10,
            //        style = getStyleFromCheckbox()
            //    )
            //        .show(this)
            //},
        )
    }

    private fun addInputDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("INPUT demos"),
            DemoItem(
                "Input demo 1",
                "Show a dialog with an input field and a hint and allow an empty input"
            ) {
                DialogInput(
                    id = 20,
                    title = "Insert your name".toText(),
                    inputHint = "E.g. Max Musterman".toText(),
                    inputInitialValue = Text.Empty,
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem(
                "Input demo 2",
                "Show a dialog with an input field and a hint AND some description AND disallow an empty input"
            ) {
                DialogInput(
                    id = 20,
                    title = "Insert your name".toText(),
                    inputDescription = "Please insert your full name here.".toText(),
                    inputHint = "E.g. Max Musterman".toText(),
                    inputInitialValue = Text.Empty,
                    inputValidator = DialogInput.InputValidatorNotEmpty,
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            }
        )
    }

/*
    private fun addListDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("LIST demos"),
            DemoItem("List demo 1", "Show a dialog with a list of items - single select") {
                DialogList(
                    30,
                    "Simple list".toText(),
                    DialogList.itemsString(List(50) { "Item ${it + 1}" }),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem("List demo 2", "Show a dialog with a list of items and icons - multi select") {
                DialogList(
                    31,
                    "Multi select".toText(),
                    DialogList.itemsString(
                        List(50) { "Item ${it + 1}" },
                        List(50) { R.mipmap.ic_launcher }),
                    selectionMode = DialogList.SelectionMode.Multi,
                    iconSize = IconSize.Medium,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "List demo 3",
                "Show a dialog with a list of items and icons, without default checkbox and custom checkbox icon"
            ) {
                DialogList(
                    32,
                    "Multi select".toText(),
                    DialogList.itemsString(
                        List(50) { "Item ${it + 1}" },
                        List(50) { R.mipmap.ic_launcher }),
                    selectionMode = DialogList.SelectionMode.Multi,
                    hideDefaultCheckMarkIcon = true,
                    checkMark = R.drawable.custom_check_mark,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "List demo 4",
                "Show a dialog with a list of items, icons, icon tint and some text - no selection but with multi click enabled - each click creates a event"
            ) {
                DialogList(
                    33,
                    "Multi click".toText(),
                    DialogList.itemsString(
                        List(50) { "Item ${it + 1}" },
                        List(50) { R.drawable.arrow_forward }),
                    text = "Some information about this dialog".toText(),
                    multiClick = true,
                    iconColorTint = Color.RED,
                    iconColorTintMode = PorterDuff.Mode.SRC_ATOP,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addNumberDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("NUMBER demos"),
            DemoItem(
                "Number demo",
                "Show a dialog with an text input field and limit input number range"
            ) {
                DialogNumber(
                    40,
                    "Age".toText(),
                    text = "Insert a value between 0 and 100".toText(),
                    hint = "Insert your age...".toText(),
                    min = 0,
                    max = 100,
                    errorMessage = "Please insert a value between 0 and 100".toText(),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "Number picker demo",
                "Shows a dialog with a number and an increase/decrease button and allows to input values from 0 to 100"
            ) {
                DialogNumberPicker(
                    41,
                    "Age".toText(),
                    25,
                    "Select your age [0, 100]".toText(),
                    min = 0,
                    max = 100,
                    step = 1,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "Number picker demo",
                "Shows a dialog with a number and an increase/decrease button and allows to input values from 0 to 100 in steps of 5 + custom value formatter"
            ) {
                DialogNumberPicker(
                    42,
                    "Age".toText(),
                    25,
                    "Select your age [0, 100], StepSize: 5".toText(),
                    min = 0,
                    max = 100,
                    step = 5,
                    valueFormatRes = R.string.number_age_formatter,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem("Multi number picker demo", "Select 3 numbers between 10 and 100") {
                DialogNumberPicker(
                    43,
                    "Select numbers".toText(),
                    10,
                    "Value 1".toText(),
                    min = 10,
                    max = 100,
                    step = 1,
                    additonalValues = arrayListOf(
                        DialogNumberPicker.NumberField("Value 2".toText(), 20),
                        DialogNumberPicker.NumberField("Value 3".toText(), 30)
                    ),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addProgressDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("PROGRESS demos"),
            DemoItem("Progress demo", "Show a progress dialog for 5s") {
                DialogProgress(
                    50,
                    title = "Loading".toText(),
                    text = "Data is loading...".toText(),
                    negButton = "Cancel".toText(),
                    dismissOnNegative = true,
                    style = getStyleFromCheckbox()
                )
                    .show(this)

                // simple unsafe method to immitate some background process...
                val handler = Handler()
                val delay = 1000L
                var c = 0
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        c++
                        DialogProgress.update("Time left: ${5 - c}s".toText())
                        if (c < 5)
                            handler.postDelayed(this, delay)
                        else
                            DialogProgress.close()
                    }
                }, delay)
            }
        )
    }

    private fun addFastAdapterDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("Fast adapter demos"),
            DemoItem(
                "Installed apps",
                "Show a list of all installed apps in a fast adapter list dialog + enable filtering via custom predicate"
            ) {
                DialogFastAdapter(
                    60,
                    AllAppsFastAdapterHelper.ItemProvider,
                    "Select an app".toText(),
                    selectionMode = DialogFastAdapter.SelectionMode.SingleClick,
                    filterPredicate = AllAppsFastAdapterHelper.FilterPredicate,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addColorDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("Color demos"),
            DemoItem("Color demo", "Show a color dialog") {
                DialogColor(
                    70,
                    "Select color".toText(),
                    color = Color.BLUE,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "Color demo",
                "Show a color dialog - with possiblility to select an alpha value"
            ) {
                DialogColor(
                    71,
                    "Select color".toText(),
                    color = ColorDefinitions.COLORS_RED.getMainColor(this), // returns main (500) red material color
                    showAlpha = true,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addDateTimeDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("Date/Time demos"),
            DemoItem("Datetime demo", "Show a date time dialog") {
                DialogDateTime(
                    80,
                    "DateTime".toText(),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addFrequencyDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("Frequency demos"),
            DemoItem("Frequency demo", "Show a frequency dialog") {
                DialogFrequency(
                    90,
                    "Frequency".toText(),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addDebugDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("DEBUGGING SETTINGS demos"),
            DemoItem("Debug settings demo", "Show a custom debug settings dialog") {

                // should be done once only, but for the demo we do it here...
                DebugDialog.init(this)

                // Dialog will save it's values inside a preference file automatically
                val items = arrayListOf<DebugDialog.Entry<*>>()
                items.addAll(
                    arrayListOf(
                        DebugDialog.Entry.Button("Debug button") {
                            Toast.makeText(this, "Debug button pressed", Toast.LENGTH_SHORT).show()
                        },
                        DebugDialog.Entry.Checkbox("Enable debug mode", "debug_bool_1", false),
                        DebugDialog.Entry.List("Debug color", "debug_list_1", 0)
                            .apply {
                                addEntries(
                                    DebugDialog.Entry.ListEntry("red", this, 0),
                                    DebugDialog.Entry.ListEntry("blue", this, 1),
                                    DebugDialog.Entry.ListEntry("green", this, 2)
                                )
                            },
                        DebugDialog.Entry.Button("Reset all debug settings") {
                            DebugDialog.reset(items, it)
                        }
                    )
                )
                DebugDialog.showDialog(
                    items,
                    this,
                    "Back",
                    true,
                    BuildConfig.DEBUG,
                    "Debug dialog"
                )
            }
        )
    }

    private fun addAdsDialogItems(adapter: ItemAdapter<IItem<*>>) {

        // this setup makes sure, that only test ads are shown!
        // This means we do not need valid ad ids in this demo either
        val TEST_SETUP = DialogAds.TestSetup()
        // this is the sample google ad mob app id for test ads - the same is defined in this apps manifest
        // should be your real app id in a real app of course
        val appId = R.string.sample_admob_app_id
        // we do not need a valid ad id for test ads - the TestSetup will provide the correct ad ids for test ads automatically in this example
        val emptyAdId = ""

        // the policy will automatically handle and update it's state if shouldShow is called
        // for the example we use the show always policy
        val policy = DialogAds.ShowPolicy.Always
        // following policies exist as well:
        // DialogAds.ShowPolicy.OnceDaily
        // DialogAds.ShowPolicy.EveryXTime(5)

        adapter.add(
            HeaderItem("Ad dialog demos"),
            DemoItem(
                "Banner dialog",
                "Shows a simple dialog with a banner - can be closed after 10s"
            ) {
                DialogAds(
                    100,
                    "Ad Banner Dialog".toText(),
                    info = "This dialog will not be shown if you buy the pro version!".toText(),
                    appId = appId.toText(),
                    bannerSetup = DialogAds.BannerSetup(
                        emptyAdId.toText() // this should be the banner ad id in a real app
                    ),
                    testSetup = TEST_SETUP,
                    style = getStyleFromCheckbox()
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            },
            DemoItem(
                "Reward dialog",
                "Shows a simple dialog with a button to show a rewarded ad - can be closed after 10s in case the ad can not be loaded"
            ) {
                DialogAds(
                    101,
                    "Ad Reward Dialog".toText(),
                    info = "This dialog will not be shown if you buy the pro version!".toText(),
                    appId = appId.toText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.toText(), // this should be the reward ad id in a real app
                        "Show me the ad".toText(),
                        DialogAds.BigAdType.Reward
                    ),
                    testSetup = TEST_SETUP,
                    style = getStyleFromCheckbox()
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            },
            DemoItem(
                "Interstitial dialog",
                "Shows a simple dialog with a button to show an interstitial ad - can be closed after 10s in case the ad can not be loaded"
            ) {
                DialogAds(
                    102,
                    "Ad Interstitial Dialog".toText(),
                    info = "This dialog will not be shown if you buy the pro version!".toText(),
                    appId = appId.toText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.toText(), // this should be the interstitial ad id in a real app
                        "Show me the ad".toText(),
                        DialogAds.BigAdType.Interstitial
                    ),
                    testSetup = TEST_SETUP,
                    style = getStyleFromCheckbox()
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            }
        )

//
    }
*/

    private var toastCounter = 0
    private var toast: Toast? = null

    private fun showToast(event: MaterialDialogEvent) {
        toastCounter++
        val dialogClass = event.javaClass.name.replace("com.michaelflisar.dialogs.", "").substringBefore("$")
        val msg = "Event #$toastCounter - $dialogClass\n$event"
        toast?.cancel()
        toast = LongToast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG)
        toast?.show()
    }

    fun MaterialDialogSetup<*, *>.showInCorrectMode(activity: MainActivity) {
        // showDialog: does not restore the fragment on recreation - it simply dismissed the dialog fragment if the system tries to recreate it
        val showAsDialog = !activity.binding.cbShowAsDialogFragment.isChecked
        if (showAsDialog) {
            showDialog(activity)
        } else {
            show(activity)
        }
    }

    @Parcelize
    class State(
        val theme: Int,
        val style: Int,
        val showAsDialogFragment: Boolean
    ) : Parcelable
}
