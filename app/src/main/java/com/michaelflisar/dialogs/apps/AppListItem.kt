package com.michaelflisar.dialogs.apps

import android.widget.ImageView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppListItem(
    override val id: Int,
    val app: App
) : DialogList.ListItem {

    @IgnoredOnParcel
    override val text: Text = app.name.asText()

    @IgnoredOnParcel
    override val subText: Text = (app.resolveInfo.activityInfo.packageName ?: "").asText()

    override fun displayIcon(imageView: ImageView): Boolean {
        // slow, but good enough for the demo... using something like Glide/Coil/Picasso would work here as well
        val icon = app.resolveInfo.loadIcon(imageView.context.packageManager)
        imageView.setImageDrawable(icon)
        return icon != null
    }

    override fun toString(): String {
        return app.name
    }
}