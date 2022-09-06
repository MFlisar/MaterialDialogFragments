package com.michaelflisar.dialogs

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ActivityMainBinding
import com.michaelflisar.dialogs.apps.AppsManager
import com.michaelflisar.dialogs.classes.DefaultFilter
import com.michaelflisar.dialogs.classes.DefaultFormatter
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.LongToast
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.dialogs.items.DemoItem
import com.michaelflisar.dialogs.items.HeaderItem
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
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
        addListDialogItems(itemAdapter)
        addNumberDialogItems(itemAdapter)
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
        onMaterialDialogEvent<DialogList.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogPicker.Event<Int>> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogPicker.Event<Float>> { event ->
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
        outState.putParcelable(
            "viewState", State(
                binding.mbTheme.checkedButtonId,
                binding.mbStyle.checkedButtonId,
                binding.cbShowAsDialogFragment.isChecked
            )
        )
    }

    // -------------------
    // helper functions for content setup
    // -------------------

    private fun getStyleFromCheckbox(): DialogStyle {
        return when (binding.mbStyle.checkedButtonId) {
            R.id.btStyleDialog -> DialogStyle.Dialog
            R.id.btStyleBottomSheet -> DialogStyle.BottomSheet()
            R.id.btStyleFullscreen -> DialogStyle.FullScreen
            else -> DialogStyle.Dialog
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

    // -------------------
    // helper functions for demo
    // -------------------

    private fun addInfoDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("INFO DEMOS"),
            DemoItem("Short Info", "Show a simple short info dialog") {
                DialogInfo(
                    id = 101,
                    title = "Info Title".asText(),
                    text = "Some info text...".asText(),
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem("Short Info", "Show a simple short info dialog with a spannable text") {
                DialogInfo(
                    id = 102,
                    title = "Info Title".asText(),
                    text = SpannableString("Here is some important information:\n\nSpannable strings are supported as well!").also {
                        it.setSpan(
                            ForegroundColorSpan(Color.RED),
                            13,
                            22,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }.asText(),
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem("Long Info", "Show a simple long info dialog") {
                DialogInfo(
                    id = 103,
                    title = "Info Title".asText(),
                    text = R.string.lorem_ipsum_long.asText(),
                    style = getStyleFromCheckbox(),
                    buttonPositive = "Accept".asText(),
                    buttonNegative = "Decline".asText()
                )
                    .showInCorrectMode(this)
            },
            //DemoItem(
            //    "Info demo with timeout",
            //    "Show a info dialog with an ok button that will only be enabled after 10s and which shows a colored warning message"
            //) {
            //    DialogInfo(
            //        11,
            //        "Info Title".asText(),
            //        "Some info about a dangerous action + 10s timeout until the ok button can be clicked.\nRotate me and I'll remember the already past by time.".asText(),
            //        warning = "Attention: Dangerous action!".asText(),
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
            HeaderItem("INPUT DEMOS"),
            DemoItem(
                "Input Demo 1",
                "Show a dialog with an input field and a hint and allow an empty input"
            ) {
                DialogInput(
                    id = 201,
                    title = "Insert your name".asText(),
                    hint = "E.g. Max Musterman".asText(),
                    initialValue = Text.Empty,
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem(
                "Input Demo 2",
                "Show a dialog with an input field and a hint AND some description AND disallow an empty input"
            ) {
                DialogInput(
                    id = 202,
                    title = "Insert your name".asText(),
                    description = "Please insert your full name here.".asText(),
                    hint = "E.g. Max Musterman".asText(),
                    initialValue = Text.Empty,
                    validator = DialogInput.InputValidatorNotEmpty,
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            },
            DemoItem(
                "Input Demo 3",
                "Show a dialog with an input field and a hint AND input type is positive number - result will still be a STRING in this case!"
            ) {
                DialogInput(
                    id = 203,
                    title = "Number".asText(),
                    description = "Please insert a number".asText(),
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    initialValue = Text.Empty,
                    validator = DialogInput.InputValidatorNotEmpty,
                    style = getStyleFromCheckbox()
                )
                    .showInCorrectMode(this)
            }
        )
    }

    private fun addListDialogItems(adapter: ItemAdapter<IItem<*>>) {
        val listItemsProvider1 = DialogList.ItemProvider.List(
            ArrayList(
                List(50) { "Item ${it + 1}" }
                    .mapIndexed { index, s ->
                        DialogList.SimpleListItem(
                            index,
                            s.asText()
                        )
                    }
            )
        )
        val listItemsProvider2 = DialogList.ItemProvider.List(
            ArrayList(
                List(50) { "Item ${it + 1}" }
                    .mapIndexed { index, s ->
                        DialogList.SimpleListItem(
                            index,
                            s.asText(),
                            resIcon = R.mipmap.ic_launcher
                        )
                    }
            ),
            //iconSize = MaterialDialogFragmentUtil.dpToPx(32) // optional, 40dp would be the default value
        )
        val listItemsProvider3 = DialogList.ItemProvider.ItemLoader(AppsManager)

        adapter.add(
            HeaderItem("LIST DEMOS"),
            DemoItem("List Demo 1", "Show a dialog with a list of items - SINGLE SELECT") {
                DialogList(
                    301,
                    "Single Select".asText(),
                    itemsProvider = listItemsProvider1,
                    description = "Select a single item...".asText(),
                    selectionMode = DialogList.SelectionMode.SingleSelect,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem("List Demo 2", "Show a dialog with a list of items - MULTI SELECT") {
                DialogList(
                    302,
                    "Multi Select".asText(),
                    itemsProvider = listItemsProvider1,
                    description = "Select multiple items...".asText(),
                    selectionMode = DialogList.SelectionMode.MultiSelect,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem("List Demo 3", "Show a dialog with a list of items - SINGLE CLICK + Icons") {
                DialogList(
                    303,
                    "Single Click".asText(),
                    itemsProvider = listItemsProvider2,
                    description = "Select a single items - the first click will emit a single event and close this dialog directly...".asText(),
                    selectionMode = DialogList.SelectionMode.SingleClick,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem("List Demo 4", "Show a dialog with a list of items - MULTI CLICK + Icons") {
                DialogList(
                    304,
                    "Multi Click".asText(),
                    itemsProvider = listItemsProvider2,
                    description = "Select multiple items, each click will emit a single event...".asText(),
                    selectionMode = DialogList.SelectionMode.MultiClick,
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "List Demo 5",
                "Show a dialog with a custom async item provider (installed apps) + filtering"
            ) {
                DialogList(
                    305,
                    "Multi Select".asText(),
                    itemsProvider = listItemsProvider3,
                    selectionMode = DialogList.SelectionMode.MultiSelect,
                    filter = DefaultFilter(
                        searchInText = true,
                        searchInSubText = true,
                        highlight = true, // highlights search term in items
                        algorithm = DefaultFilter.Algorithm.String, // either search for items containing all words or the search term as a whole
                        ignoreCase = true,
                        unselectInvisibleItems = true // true means, items are unselected as soon as they are filtered out and get invisible for the user
                    ),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addNumberDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("NUMBER DEMOS"),
            DemoItem(
                "Number Demo 1",
                "Show a dialog that allows selecting an integer value in the range [0, 100] - BUTTON STYLE"
            ) {
                DialogPicker<Int>(
                    401,
                    "Age".asText(),
                    value = 18,
                    description = "Select a value between 0 and 100".asText(),
                    setup = DialogPicker.Setup<Int>(
                        0,
                        100,
                        1
                    ),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "Number Demo 2",
                "Show a dialog that allows selecting an integer value in the range [0, 100] in steps of 5 + value formatter"
            ) {
                DialogPicker<Int>(
                    402,
                    "Value".asText(),
                    value = 50,
                    description = "Select a value between 0 and 100 in steps of 5".asText(),
                    setup = DialogPicker.Setup<Int>(
                        0,
                        100,
                        5,
                        DefaultFormatter<Int>(R.string.custom_int_formatter)
                    ),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            },
            DemoItem(
                "Number Demo 3",
                "Show a dialog that allows selecting a float value in the range [0, 10] in steps of 0.5"
            ) {
                DialogPicker<Float>(
                    403,
                    "Value".asText(),
                    value = 5f,
                    description = "Select a value between 0 and 10 in steps of 0.5".asText(),
                    setup = DialogPicker.Setup<Float>(
                        0f,
                        10f,
                        0.5f
                    ),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }
/*
    private fun addProgressDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("PROGRESS DEMOS"),
            DemoItem("Progress demo", "Show a progress dialog for 5s") {
                DialogProgress(
                    50,
                    title = "Loading".asText(),
                    text = "Data is loading...".asText(),
                    negButton = "Cancel".asText(),
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
                        DialogProgress.update("Time left: ${5 - c}s".asText())
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
            HeaderItem("Fast adapter DEMOS"),
            DemoItem(
                "Installed apps",
                "Show a list of all installed apps in a fast adapter list dialog + enable filtering via custom predicate"
            ) {
                DialogFastAdapter(
                    60,
                    AllAppsFastAdapterHelper.ItemProvider,
                    "Select an app".asText(),
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
            HeaderItem("COLOR DEMOS"),
            DemoItem("Color demo", "Show a color dialog") {
                DialogColor(
                    70,
                    "Select color".asText(),
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
                    "Select color".asText(),
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
            HeaderItem("DATE/TIME DEMOS"),
            DemoItem("Datetime demo", "Show a date time dialog") {
                DialogDateTime(
                    80,
                    "DateTime".asText(),
                    style = getStyleFromCheckbox()
                )
                    .show(this)
            }
        )
    }

    private fun addFrequencyDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("FREQUENCY DEMOS"),
            DemoItem("Frequency demo", "Show a frequency dialog") {
                DialogFrequency(
                    90,
                    "Frequency".asText(),
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
                    "Ad Banner Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bannerSetup = DialogAds.BannerSetup(
                        emptyAdId.asText() // this should be the banner ad id in a real app
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
                    "Ad Reward Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.asText(), // this should be the reward ad id in a real app
                        "Show me the ad".asText(),
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
                    "Ad Interstitial Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.asText(), // this should be the interstitial ad id in a real app
                        "Show me the ad".asText(),
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
        val dialogClass =
            event.javaClass.name.replace("com.michaelflisar.dialogs.", "").substringBefore("$")
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
