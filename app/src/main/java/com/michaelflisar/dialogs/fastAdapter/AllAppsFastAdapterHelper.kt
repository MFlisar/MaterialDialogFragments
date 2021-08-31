package com.michaelflisar.dialogs.fastAdapter

import android.content.Context
import com.michaelflisar.dialogs.classes.App
import com.michaelflisar.dialogs.setups.DialogFastAdapter
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

object AllAppsFastAdapterHelper {

    @Parcelize
    object FilterPredicate : DialogFastAdapter.IFilterPredicate<SimpleAppItem> {
        override fun filter(item: SimpleAppItem, filter: String): Boolean {
            return filter.isEmpty() || item.app.name.contains(filter, true)
        }
    }

    @Parcelize
    object ItemProvider : DialogFastAdapter.IItemProvider<SimpleAppItem> {

        @IgnoredOnParcel
        override val loadItemsAsynchronous = true

        override fun loadItems(context: Context): List<SimpleAppItem> {
            return ArrayList(App.load(context).map { SimpleAppItem(it) })
        }
    }
}