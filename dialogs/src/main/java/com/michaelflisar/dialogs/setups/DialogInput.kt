package com.michaelflisar.dialogs.setups

import android.os.Bundle
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogInputFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class DialogInput(
        // base setup
        override val id: Int,
        override val title: Text,
        val inputType: Int,
        val input: InputField,
        override val posButton: Text = Text.TextRes(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,

        // special setup
        val neutralButtonMode: NeutralButtonMode = NeutralButtonMode.SendEvent,
        val textToInsertOnNeutralButtonClick: Text? = null,
        val allowEmptyText: Boolean = false,
        val minLines: Int = -1,
        val textSize: Float? = null,
        val inputTextSize: Float? = null,
        val selectText: Boolean = false,
        val additonalInputs: ArrayList<InputField> = arrayListOf()
) : BaseDialogSetup {

    override fun create(): DialogFragment = DialogInputFragment.create(this)

    @Parcelize
    class InputField(val label: Text?, val initialText: Text?, val hint: Text?) : Parcelable

    enum class NeutralButtonMode {
        SendEvent,
        InsertText
    }
}