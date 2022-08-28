package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.MaterialDialogEvent
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogList(
    // Key
    override val id: Int?,
    // Title
    override val title: Text,
    // specific fields
    val listItemsProvider: ItemProvider,
    val listDescription: Text = Text.Empty,
    val listSelectionMode: SelectionMode = SelectionMode.SingleSelect,
    // TODO
    // ...
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Behaviour / Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    override val style: DialogStyle = MaterialDialog.defaults.style,
    override val swipeDismissable: Boolean = MaterialDialog.defaults.swipeDismissable,
    //override val customTheme: Int? = null,
    // Attached Data
    override val extras: Bundle? = null
) : MaterialDialogSetup<DialogList, DialogListFragment>() {

    sealed class Event : MaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extras: Bundle?,
            val selectedItems: List<ListItem>,
            val button: MaterialDialogButton?
        ) : Event()

        data class Cancelled(override val id: Int?, override val extras: Bundle?) : Event()
    }

    override fun createFragment(
        showAsDialog: Boolean
    ) = DialogListFragment.create(this, showAsDialog)

    // -----------
    // Events
    // -----------

    override fun onCancelled() {
        MaterialDialog.sendEvent(Event.Cancelled(this.id, this.extras))
    }

    override fun onButton(
        fragment: DialogListFragment,
        button: MaterialDialogButton
    ): Boolean {
        val selectedItems = fragment.getSelectedItems()
        MaterialDialog.sendEvent(Event.Result(this.id, this.extras, selectedItems, button))
        return true
    }

    fun sendEvent(
        item: ListItem
    ) {
        MaterialDialog.sendEvent(Event.Result(this.id, this.extras, listOf(item), null))
    }

    // -----------
    // Interfaces/Classes
    // -----------

    interface ListItem : Parcelable {
        val text: Text
        val icon: ((imageView: ImageView) -> Unit)?
    }

    @Parcelize
    class SimpleListItem(
        override val text: Text,
        val resIcon: Int? = null
    ) : ListItem {
        override val icon: ((imageView: ImageView) -> Unit) = { iv ->
            resIcon?.let { iv.setImageResource(it) }
        }
    }

    interface ListItemLoader : Parcelable {
        suspend fun load(): List<ListItem>
    }

    sealed class ItemProvider : Parcelable {

        abstract val iconSize: Int

        @Parcelize
        class List(
            val items: ArrayList<ListItem>,
            override val iconSize: Int = MaterialDialogFragmentUtil.dpToPx(32)
        ) : ItemProvider()

        @Parcelize
        class ItemLoader(
            val loader: ListItemLoader,
            override val iconSize: Int = MaterialDialogFragmentUtil.dpToPx(32)
        ) : ItemProvider()
    }

    enum class SelectionMode {
        SingleSelect,
        MultiSelect,
        SingleClick,
        MultiClick
    }
}