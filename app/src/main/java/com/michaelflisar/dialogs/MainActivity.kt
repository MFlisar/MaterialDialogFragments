package com.michaelflisar.dialogs

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ActivityMainBinding
import com.michaelflisar.dialogs.classes.asText
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.events.*
import com.michaelflisar.dialogs.interfaces.DialogFragmentCallback
import com.michaelflisar.dialogs.setups.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter


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
    }

    /*
     * optionally handle the results of the dialog - use the event.id to find out where the event comes from
     */
    override fun onDialogResultAvailable(event: BaseDialogEvent) {
        val data = when (event) {
            is DialogInfoEvent -> "Info dialog closed\nButton: ${event.buttonIndex}"
            is DialogInputEvent -> {
                when (event) {
                    is DialogInputEvent.Input -> "Input dialog closed\nInput: ${event.getInput()} | ${event.getInput(1)}"
                    is DialogInputEvent.NeutralButton -> "Input dialog closed\nClosed by neutral button click"
                }
            }
            is DialogNumberEvent -> "Number dialog closed\nInput: ${event.value} (${event.values})"
            is DialogProgressEvent -> {
                when (event) {
                    is DialogProgressEvent.Closed -> "Progress dialog closed\nProgress finished"
                    is DialogProgressEvent.Cancelled -> "Progress dialog closed\nCANCELLED"
                }
            }
            is DialogListEvent -> "List dialog event\nIndizes: ${event.indizes.joinToString()}"
            else -> null
        }
        Toast.makeText(this, "Event: ${event.id}\n$data", Toast.LENGTH_LONG).show()
    }

    // -------------------
    // helper functions for content setup
    // -------------------

    private fun initRecyclerView(): ItemAdapter<IItem<*, *>> {
        val itemAdapter = ItemAdapter<IItem<*, *>>()
        val adapter = FastAdapter.with<IItem<*, *>, ItemAdapter<IItem<*, *>>>(itemAdapter)
                .withSelectable(true)
                .withOnClickListener { _, _, item, _ ->
                    if (item is DemoItem) {
                        item.function(item)
                    }
                    true
                }
        binding.rvDemoItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvDemoItems.adapter = adapter
        return itemAdapter
    }

    private fun addInfoDialogItems(adapter: ItemAdapter<IItem<*, *>>) {
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
                })
    }

    private fun addInputDialogItems(adapter: ItemAdapter<IItem<*, *>>) {
        adapter.add(
                HeaderItem("INPUT demos"),
                DemoItem("Input demo 1", "Show a dialog with an input field and a hint and allow an empty input") {
                    DialogInput(
                            20,
                            "Insert your name".asText(),
                            InputType.TYPE_CLASS_TEXT,
                            DialogInput.InputField("Please insert your full name".asText(), null, "E.g. Max Musterman".asText()),
                            allowEmptyText = true
                    )
                            .create()
                            .show(this)
                },
                DemoItem("Input demo 2", "Show a dialog with 2 input fields and force non empty inputs") {
                    DialogInput(
                            21,
                            "Insert your name".asText(),
                            InputType.TYPE_CLASS_TEXT,
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
                            InputType.TYPE_CLASS_TEXT,
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

    private fun addListDialogItems(adapter: ItemAdapter<IItem<*, *>>) {
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
                DemoItem("List demo 4", "Show a dialog with a list of items and icons and icon tint - no selection but with multi click enabled - each click creates a event") {
                    DialogList(
                            33,
                            "Multi click".asText(),
                            DialogList.itemsString(List(50) { "Item ${it + 1}" }, List(50) { R.drawable.ic_arrow_forward_black_24dp }),
                            multiClick = true,
                            iconColorTint = Color.RED,
                            iconColorTintMode = PorterDuff.Mode.SRC_ATOP
                    )
                            .create()
                            .show(this)
                }
        )
    }

    private fun addNumberDialogItems(adapter: ItemAdapter<IItem<*, *>>) {
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

    private fun addProgressDialogItems(adapter: ItemAdapter<IItem<*, *>>) {
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

}
