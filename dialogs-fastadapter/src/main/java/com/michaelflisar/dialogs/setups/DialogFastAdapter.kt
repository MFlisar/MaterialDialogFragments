package com.michaelflisar.dialogs.setups

import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text

abstract class DialogFastAdapter(
        // base setup
        override val id: Int,
        override val title: Text,
        val text: Text,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val darkTheme: Boolean = false,
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,

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
) : BaseDialogSetup