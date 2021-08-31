package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.enums.EndType
import kotlinx.parcelize.Parcelize
import java.io.Serializable

sealed class End(
    val endType: EndType
) : Parcelable, Serializable {

    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 1
    }

    @Parcelize
    object Never : End(EndType.Forever)

    @Parcelize
    class Times(val times: Int) : End(EndType.UntilTimes)

    @Parcelize
    class Date(val timeInMillis: Long) : End(EndType.UntilDate)
}