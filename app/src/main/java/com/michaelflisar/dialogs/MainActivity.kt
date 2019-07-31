package com.michaelflisar.dialogs

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ActivityMainBinding
import com.michaelflisar.dialogs.classes.asText
import com.michaelflisar.dialogs.debug.DebugDialog
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.events.*
import com.michaelflisar.dialogs.fastAdapter.AllAppsFastAdapterDialog
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback
import com.michaelflisar.dialogs.setups.*
import com.michaelflisar.dialogs.utils.ColorUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), DialogFragmentCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val itemAdapter = initRecyclerView()
        addInfoDialogItems(itemAdapter)
        addInputDialogItems(itemAdapter)
        addListDialogItems(itemAdapter)
        addNumberDialogItems(itemAdapter)
        addProgressDialogItems(itemAdapter)
        addFastAdapterDialogItems(itemAdapter)
        addColorDialogItems(itemAdapter)
        addDateTimeDialogItems(itemAdapter)
        addFrequencyDialogItems(itemAdapter)
        addDebugDialogItems(itemAdapter)
        addAdsDialogItems(itemAdapter)
    }

    /*
     * optionally handle the results of the dialog - use the event.id to find out where the event comes from
     */
    override fun onDialogResultAvailable(event: BaseDialogEvent): Boolean {

        // we have enabled this manually in this demo, by default cancel events are not send!
        // useful if you want to close the parent activity if a special dialog is cancelled or do something else based on this event
        if (event is DialogCancelledEvent) {
            Toast.makeText(this, "Dialog (ID ${event.id}) cancelled via touch outside or back button press!", Toast.LENGTH_LONG).show()
            return true
        }

        // depending on your dialog setup distinguish between event.posClicked(), event.neutrClicked() and event.negClicked()
        // depending on your dialog setup it may happen that no button was clicked, e.g. the list dialog allows to send events on every item click
        // if you are only interested in any data, simply do following:
        //
        // event.data?.let {
        //    // some data is available for sure, either because pos button was clicked or because dialog setup defines, that data should be reported
        // }
        
        val data = when (event) {
            is DialogInfoEvent -> "Info dialog closed - ID = ${event.id}"
            is DialogInputEvent -> {
                if (event.neutrClicked()) {
                    "Input dialog closed\nClosed by neutral button click"
                } else {
                    "Input dialog closed\nInput: ${event.data?.getInput()} | ${event.data?.getInput(1)}"
                }
            }
            is DialogNumberEvent -> "Number dialog closed\nInput: ${event.data?.value} (${event.data?.values})"
            is DialogProgressEvent -> {
                if (event.negClicked()) {
                    "Progress dialog closed\nCANCELLED"
                } else {
                    "Progress dialog closed\nProgress finished"
                }
            }
            is DialogListEvent -> "List dialog event\nIndizes: ${event.data?.indizes?.joinToString()}"
            is DialogColorEvent -> "Color dialog event\nColor: ${ColorUtil.getColorAsARGB(event.data!!.color)}"
            is DialogDateTimeEvent -> {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = event.data?.date?.let { format.format(it.time) } ?: "NULL"
                "Date time event\nSelected date: $date"
            }
            is DialogFrequencyEvent -> {
                val frequency = event.data?.frequency?.toReadableString(this)
                "Frequency event\nSelected frequency: $frequency"
            }
            is DialogAdsEvent -> {
                val data = event.data
                when (data) {
                    is DialogAdsEvent.Data.RewardReceived -> {
                        "Reward received - amount: ${data.amount}"
                    }
                    is DialogAdsEvent.Data.InterstitialShown -> {
                        "Interstitial shown"
                    }
                    is DialogAdsEvent.Data.ClosedByUser -> {
                        "Closed by user"
                    }
                }
            }
            else -> return false
        }
        Toast.makeText(this, "Event: ${event.id}\n$data", Toast.LENGTH_LONG).show()
        return true
    }

    // -------------------
    // helper functions for content setup
    // -------------------

    private fun initRecyclerView(): ItemAdapter<IItem<*>> {
        val itemAdapter = ItemAdapter<IItem<*>>()
        val adapter = FastAdapter.with(itemAdapter)

        val selectExtension = adapter.getSelectExtension()
        selectExtension.isSelectable = true

        adapter.onClickListener = { _, _, item, _ ->
            if (item is DemoItem) {
                item.function(item)
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
                DemoItem("Simple info demo", "Show a simple info dialog") {
                    DialogInfo(
                            10,
                            "Info Title".asText(),
                            "Some info label".asText()
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Info demo with timeout", "Show a info dialog with an ok button that will only be enabled after 10s and which shows a colored warning message") {
                    DialogInfo(
                            11,
                            "Info Title".asText(),
                            "Some info about a dangerous action + 10s timeout until the ok button can be clicked.\nRotate me and I'll remember the already past by time.".asText(),
                            warning = "Attention: Dangerous action!".asText(),
                            warningSeparator = "\n\n",
                            cancelable = false,
                            timerPosButton = 10
                    )
                            .create()
                            .show(this)
                },
                DemoItem("HTLM info demo", "Show a simple info dialog with HTML text") {
                    DialogInfo(
                            12,
                            "Info Title".asText(),
                            "<b>Header</b></br>Some text with a bold html text title inside text and some <font color=\"#FF0000\">red colored text</font> inside it.</br></br><b>Header2</b></br>This version of <font color=\"#00FF00\">InfoDialog</font> supports <u>all</u> html tags that are supported by a WebView &#128526;.".asText(),
                            textIsHtml = true
                    )
                            .create()
                            .show(this)
                })
    }

    private fun addInputDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
                HeaderItem("INPUT demos"),
                DemoItem("Input demo 1", "Show a dialog with an input field and a hint and allow an empty input") {
                    DialogInput(
                            20,
                            "Insert your name".asText(),
                            DialogInput.InputField("Please insert your full name".asText(), null, "E.g. Max Musterman".asText(), true)
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Input demo 2", "Show a dialog with 2 input fields and force non empty inputs") {
                    DialogInput(
                            21,
                            "Insert your name".asText(),
                            DialogInput.InputField("First name".asText(), null, "E.g. Max".asText()),
                            additonalInputs = arrayListOf(DialogInput.InputField("Last name".asText(), null, "E.g. Musterman".asText()))
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Input demo 3", "Show a dialog with an input field and a button to insert some predefined placeholder") {
                    DialogInput(
                            22,
                            "Insert the macro".asText(),
                            DialogInput.InputField(null, null, "My name is %s".asText()),
                            neutralButtonMode = DialogInput.NeutralButtonMode.InsertText,
                            neutrButton = "Insert %s".asText(),
                            textToInsertOnNeutralButtonClick = "%s".asText()
                    )
                            .create()
                            .show(this)
                }
        )
    }

    private fun addListDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
                HeaderItem("LIST demos"),
                DemoItem("List demo 1", "Show a dialog with a list of items - single select") {
                    DialogList(
                            30,
                            "Simple list".asText(),
                            DialogList.itemsString(List(50) { "Item ${it + 1}" })
                    )
                            .create()
                            .show(this)
                },
                DemoItem("List demo 2", "Show a dialog with a list of items and icons - multi select") {
                    DialogList(
                            31,
                            "Multi select".asText(),
                            DialogList.itemsString(List(50) { "Item ${it + 1}" }, List(50) { R.mipmap.ic_launcher }),
                            selectionMode = DialogList.SelectionMode.Multi,
                            iconSize = IconSize.Medium
                    )
                            .create()
                            .show(this)
                },
                DemoItem("List demo 3", "Show a dialog with a list of items and icons, without default checkbox and custom checkbox icon") {
                    DialogList(
                            32,
                            "Multi select".asText(),
                            DialogList.itemsString(List(50) { "Item ${it + 1}" }, List(50) { R.mipmap.ic_launcher }),
                            selectionMode = DialogList.SelectionMode.Multi,
                            hideDefaultCheckMarkIcon = true,
                            checkMark = R.drawable.custom_check_mark
                    )
                            .create()
                            .show(this)
                },
                DemoItem("List demo 4", "Show a dialog with a list of items, icons, icon tint and some text - no selection but with multi click enabled - each click creates a event") {
                    DialogList(
                            33,
                            "Multi click".asText(),
                            DialogList.itemsString(List(50) { "Item ${it + 1}" }, List(50) { R.drawable.ic_arrow_forward_black_24dp }),
                            text = "Some information about this dialog".asText(),
                            multiClick = true,
                            iconColorTint = Color.RED,
                            iconColorTintMode = PorterDuff.Mode.SRC_ATOP
                    )
                            .create()
                            .show(this)
                }
        )
    }

    private fun addNumberDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
                HeaderItem("NUMBER demos"),
                DemoItem("Number demo", "Show a dialog with an text input field and limit input number range") {
                    DialogNumber(
                            40,
                            "Age".asText(),
                            text = "Insert a value between 0 and 100".asText(),
                            hint = "Insert your age...".asText(),
                            min = 0,
                            max = 100,
                            errorMessage = "Please insert a value between 0 and 100".asText()
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Number picker demo", "Shows a dialog with a number and an increase/decrease button and allows to input values from 0 to 100") {
                    DialogNumberPicker(
                            41,
                            "Age".asText(),
                            25,
                            "Select your age [0, 100]".asText(),
                            min = 0,
                            max = 100,
                            step = 1
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Number picker demo", "Shows a dialog with a number and an increase/decrease button and allows to input values from 0 to 100 in steps of 5 + custom value formatter") {
                    DialogNumberPicker(
                            42,
                            "Age".asText(),
                            25,
                            "Select your age [0, 100], StepSize: 5".asText(),
                            min = 0,
                            max = 100,
                            step = 5,
                            valueFormatRes = R.string.number_age_formatter
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Multi number picker demo", "Select 3 numbers between 10 and 100") {
                    DialogNumberPicker(
                            43,
                            "Select numbers".asText(),
                            10,
                            "Value 1".asText(),
                            min = 10,
                            max = 100,
                            step = 1,
                            additonalValues = arrayListOf(
                                    DialogNumberPicker.NumberField("Value 2".asText(), 20),
                                    DialogNumberPicker.NumberField("Value 3".asText(), 30)
                            )
                    )
                            .create()
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
                            "Loading".asText(),
                            "Data is loading...".asText(),
                            negButton = "Cancel".asText(),
                            dismissOnNegative = true
                    )
                            .create()
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
                HeaderItem("Fast adapter demos"),
                DemoItem("Installed apps", "Show a list of all installed apps in a fast adapter list dialog + enable filtering via custom predicate") {
                    val dialog = AllAppsFastAdapterDialog.create(
                            AllAppsFastAdapterDialog.Setup(
                                    DialogFastAdapter.InternalSetup(
                                            60,
                                            "Select app".asText(),
                                            clickable = true,
                                            dismissOnClick = true,
                                            filterable = true
                                    )
                            )
                    )
                    dialog.show(this)
                }
        )
    }

    private fun addColorDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
                HeaderItem("Color demos"),
                DemoItem("Color demo", "Show a color dialog") {
                    DialogColor(
                            70,
                            "Select color".asText(),
                            color = Color.BLUE
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Color demo", "Show a color dialog - with possiblility to select an alpha value") {
                    DialogColor(
                            71,
                            "Select color".asText(),
                            color = ColorUtil.COLORS_RED.getMainColor(this), // returns main (500) red material color
                            showAlpha = true
                    )
                            .create()
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
                            "DateTime".asText()
                    )
                            .create()
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
                            "Frequency".asText()
                    )
                            .create()
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
                DemoItem("Banner dialog", "Shows a simple dialog with a banner - can be closed after 10s") {
                    if (policy.shouldShow(this)) {
                        DialogAds(
                                100,
                                "Ad Banner Dialog".asText(),
                                info = "This dialog will not be shown if you buy the pro version!".asText(),
                                appId = appId.asText(),
                                bannerSetup = DialogAds.BannerSetup(
                                        emptyAdId.asText() // this should be the banner ad id in a real app
                                ),
                                testSetup = TEST_SETUP
                        )
                                .create()
                                .show(this)
                    }
                },
                DemoItem("Reward dialog", "Shows a simple dialog with a button to show a rewarded ad - can be closed after 10s in case the ad can not be loaded") {
                    if (policy.shouldShow(this)) {
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
                                testSetup = TEST_SETUP
                        )
                                .create()
                                .show(this)
                    }
                },
                DemoItem("Interstitial dialog", "Shows a simple dialog with a button to show an interstitial ad - can be closed after 10s in case the ad can not be loaded") {
                    if (policy.shouldShow(this)) {
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
                                testSetup = TEST_SETUP
                        )
                                .create()
                                .show(this)
                    }
                }
        )

//
    }

}
