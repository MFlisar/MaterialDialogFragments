package com.michaelflisar.dialogs.setups

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.dialogs.fragments.DialogFastAdapterFragment
import com.michaelflisar.text.Text
import com.mikepenz.fastadapter.IItem
import kotlinx.android.parcel.Parcelize

@Parcelize
class DialogFastAdapter<Item : IItem<*>>(
        // base setup
        override val id: Int,
        val itemProvider: IItemProvider<Item>,
        override val title: Text?,
        override val posButton: Text = Text.Resource(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val style: DialogStyle = DialogStyle.Dialog,

        // special setup
        val selectionMode: SelectionMode = SelectionMode.SingleSelect,
        val layoutStyle: LayoutStyle = LayoutStyle.List(),
        val info: Text? = null,
        val infoSize: Float? = null,
        val filterPredicate: IFilterPredicate<Item>? = null,
        val withToolbar: Boolean = false
) : BaseDialogSetup {

    sealed class LayoutStyle : Parcelable {
        abstract @RecyclerView.Orientation
        val orientation: Int
        abstract val reverseLayout: Boolean

        @Parcelize
        class List(
                @RecyclerView.Orientation override val orientation: Int = RecyclerView.VERTICAL,
                override val reverseLayout: Boolean = false
        ) : LayoutStyle()

        @Parcelize
        class Grid(
                val columns: Int,
                @RecyclerView.Orientation override val orientation: Int = RecyclerView.VERTICAL,
                override val reverseLayout: Boolean = false
        ) : LayoutStyle()
    }

    enum class SelectionMode {
        SingleClick,
        SingleSelect,
        MultiSelect
    }

    override fun create() = DialogFastAdapterFragment.create(this)

    interface IItemProvider<Item : IItem<*>> : Parcelable {
        val loadItemsAsynchronous: Boolean
        fun loadItems(context: Context): List<Item>
    }

    interface IFilterPredicate<Item : IItem<*>> : Parcelable {
        fun filter(item: Item, filter: String): Boolean
    }
}