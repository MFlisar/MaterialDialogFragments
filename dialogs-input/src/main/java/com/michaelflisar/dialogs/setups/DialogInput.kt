package com.michaelflisar.dialogs.setups

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.DialogStyle
import com.michaelflisar.text.Text
import com.michaelflisar.dialogs.fragments.DialogInputFragment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class DialogInput(
        // base setup
        override val id: Int,
        override val title: Text?,
        val input: InputField,
        override val posButton: Text = Text.Resource(android.R.string.ok),
        override val negButton: Text? = null,
        override val neutrButton: Text? = null,
        override val cancelable: Boolean = true,
        override val extra: Bundle? = null,
        override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,
        override val style: DialogStyle = DialogStyle.Dialog,

        // special setup
        val neutralButtonMode: NeutralButtonMode = NeutralButtonMode.SendEvent,
        val textToInsertOnNeutralButtonClick: Text? = null,
        val minLines: Int = -1,
        val textSize: Float? = null,
        val inputTextSize: Float? = null,
        val selectText: Boolean = false,
        val additonalInputs: ArrayList<InputField> = arrayListOf()
) : BaseDialogSetup {

    override fun create() = DialogInputFragment.create(this)

    @Parcelize
    class InputField(val label: Text? = null, val initialText: Text? = null, val hint: Text? = null, val allowEmptyText: Boolean = false, val inputType: Int = InputType.TYPE_CLASS_TEXT) : Parcelable

    enum class NeutralButtonMode {
        SendEvent,
        InsertText
    }
}