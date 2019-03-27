package com.michaelflisar.dialogs.setups

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import kotlinx.android.parcel.Parcelize

abstract class DialogFastAdapter(
    val internalSetup: InternalSetup
) : BaseDialogSetup {

    override val id: Int = internalSetup.id
    override val title: Text = internalSetup.title
    override val posButton: Text = internalSetup.posButton
    override val negButton: Text? = internalSetup.negButton
    override val neutrButton: Text? = internalSetup.neutrButton
    override val cancelable: Boolean = internalSetup.cancelable
    override val extra: Bundle? = internalSetup.extra

    @Parcelize
    class InternalSetup(
            // base setup
            val id: Int,
            val title: Text,
            val posButton: Text = Text.TextRes(android.R.string.ok),
            val negButton: Text? = null,
            val neutrButton: Text? = null,
            val cancelable: Boolean = true,
            val extra: Bundle? = null,

            // special setup
            val clickable: Boolean = false,
            val dismissOnClick: Boolean = false,
            val info: Text? = null,
            val infoSize: Float? = null,
            /*
            Dialog MUST implement [com.mikepenz.fastadapter.IItemAdapter.Predicate]&lt;[IItem]&gt; for this to work!
             */
            val filterable: Boolean = false,
            val withToolbar: Boolean = false
    ) : Parcelable
}