package com.michaelflisar.dialogs.fastAdapter

import com.michaelflisar.dialogs.classes.App
import com.michaelflisar.dialogs.fragments.DialogFastAdapterFragment
import com.michaelflisar.dialogs.setups.DialogFastAdapter
import com.mikepenz.fastadapter.IItem
import kotlinx.android.parcel.Parcelize
import java.util.*

/*
 IPredicate is optional, but must be used if filtering is enabled like in this example!
 */
class AllAppsFastAdapterDialog : DialogFastAdapterFragment(), DialogFastAdapterFragment.IPredicate<IItem<*>> {
    override fun filter(item: IItem<*>, constraint: CharSequence?): Boolean {
        return (item as SimpleAppItem).app.name.contains(constraint ?: "", true)
    }

    companion object {
        fun create(setup: Setup): AllAppsFastAdapterDialog {
            return DialogFastAdapterFragment.create(setup) { AllAppsFastAdapterDialog() }
        }
    }

    override fun createData(): ArrayList<IItem<*>> {
        return ArrayList(App.loadAndCache(context!!).map { SimpleAppItem(it) })
    }

    @Parcelize
    class Setup(val setup: InternalSetup) : DialogFastAdapter(setup) {
        override fun create() = AllAppsFastAdapterDialog.create(this)
    }
}