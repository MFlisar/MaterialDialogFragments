package com.michaelflisar.dialogs.setups

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.fragments.DialogListFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import com.michaelflisar.dialogs.interfaces.IParcelableTextImageProvider
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DialogList(
        // base setup
        override val id: Int,
        override val title: Text?,
        val items: List<Item>,
        val text: Text? = null,
        val selectionMode: SelectionMode = SelectionMode.None,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val extra: Bundle? = null,

        // special setup
        val multiClick: Boolean = false,
        val initialSingleSelection: Int? = null,
        val initialMultiSelection: IntArray = IntArray(0),
        val iconSize: IconSize = IconSize.Small,
        val onlyShowIconIfItemIsSelected: Boolean = false,
        val hideDefaultCheckMarkIcon: Boolean = false, // hide default check mark if item itself is able to display checked state
        val noImageVisibility: Int = View.INVISIBLE,
        val iconColorTint: Int? = null,
        val iconColorTintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_ATOP,
        val checkMark: Int? = null

) : BaseDialogSetup {

    override fun create() = DialogListFragment.create(this)

    enum class SelectionMode {
        None,
        Single,
        Multi
    }

    sealed class Item : Parcelable {
        @Parcelize
        class Simple(val text: String?) : Item()

        @Parcelize
        class SimpleWithIcon(val text: String?, val icon: Int) : Item()

        /*
         if parcelable implements IParcelableTextImageProvider, interface will be used, otherwise it will be used as string items (text is retrieved via item.toString())
         */
        @Parcelize
        class Custom(val parcelable: Parcelable) : Item()
    }

    companion object {
        fun itemsString(items: List<String>): List<Item> = items.map { Item.Simple(it) }

        fun itemsString(items: List<String>, icons: List<Int>): List<Item> {
            if (items.size != icons.size)
                throw RuntimeException("items and icons must contain identical number of items!")
            val list = arrayListOf<Item>()
            for (i in 0 until items.size) {
                list.add(Item.SimpleWithIcon(items[i], icons[i]))
            }
            return list
        }

        fun itemsParcelable(items: List<IParcelableTextImageProvider>): List<Item> = items.map { Item.Custom(it) }
    }
}