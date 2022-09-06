package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
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
    val itemsProvider: ItemProvider,
    val description: Text = Text.Empty,
    val selectionMode: SelectionMode = SelectionMode.SingleSelect,
    val filter: Filter? = null,
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
        val selectedItems = fragment.getSelectedItemsForResult()
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

    enum class SelectionMode {
        SingleSelect,
        MultiSelect,
        SingleClick,
        MultiClick
    }

    interface ListItem : Parcelable {
        val id: Int
        val text: Text
        val subText: Text
        fun displayIcon(imageView: ImageView): Boolean
        override fun equals(other: Any?): Boolean
    }

    interface Loader : Parcelable {
        suspend fun load(context: Context): List<ListItem>
    }

    @Parcelize
    data class SimpleListItem(
        override val id: Int,
        override val text: Text,
        override val subText: Text = Text.Empty,
        val resIcon: Int? = null
    ) : ListItem {
        override fun displayIcon(imageView: ImageView): Boolean {
            resIcon?.let { imageView.setImageResource(it) }
            return resIcon != null
        }
    }

    sealed class ItemProvider : Parcelable {

        abstract val iconSize: Int

        @Parcelize
        class List(
            val items: ArrayList<ListItem>,
            override val iconSize: Int = MaterialDialogFragmentUtil.dpToPx(40)
        ) : ItemProvider()

        @Parcelize
        class ItemLoader(
            val loader: Loader,
            override val iconSize: Int = MaterialDialogFragmentUtil.dpToPx(40)
        ) : ItemProvider()
    }

    interface Filter : Parcelable {
        val unselectInvisibleItems: Boolean
        fun matches(context: Context, item: ListItem, filter: String): Boolean
        fun displayText(tv: TextView, item: ListItem, filter: String): CharSequence
        fun displaySubText(tv: TextView, item: ListItem, filter: String): CharSequence
    }
}