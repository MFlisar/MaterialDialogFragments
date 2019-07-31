package com.michaelflisar.dialogs.setups

import android.os.Bundle
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogDateTimeFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * [minDateTime] - minimal valid date
 *
 * [maxDateTime] - maximal valid date (Type.DateOnly only)
 *
 * [currentDateTime] - initial date
 *
 * [requireFutureDateTime] - force a date in the future
 *
 * [show24HoursView] - 24h view enabled
 */
@Parcelize
class DialogDateTime(
    // base setup
    override val id: Int,
    override val title: Text? = null,
    override val posButton: Text = Text.TextRes(android.R.string.ok),
    override val negButton: Text? = null,
    override val neutrButton: Text? = null,
    override val cancelable: Boolean = true,
    override val extra: Bundle? = null,
    override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,

    // special setup
    val type: Type = Type.DateAndTime,
    val minDateTime: Calendar? = null,
    val maxDateTime: Calendar? = null,
    val currentDateTime: Calendar? = null,
    val requireFutureDateTime: Boolean = false,
    val show24HoursView: Boolean = true
) : BaseDialogSetup {

    override fun create() = DialogDateTimeFragment.create(this)

    enum class Type {
        DateOnly,
        TimeOnly,
        DateAndTime
    }
}