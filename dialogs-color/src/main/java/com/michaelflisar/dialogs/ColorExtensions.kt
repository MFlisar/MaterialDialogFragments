package com.michaelflisar.dialogs

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.michaelflisar.dialogs.color.R

fun View.setCircleBackground(color: Int, withBorder: Boolean) {
    var drawable: GradientDrawable? = null
    drawable = if (withBorder) {
        ContextCompat.getDrawable(context, if (DialogSetup.isUsingDarkTheme(context)) R.drawable.circle_with_border_dark else R.drawable.circle_with_border_light) as GradientDrawable
    } else {
        ContextCompat.getDrawable(context, R.drawable.circle) as GradientDrawable
    }
    drawable.setColor(color)
    background = drawable
}

fun ImageView.tint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun Fragment.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE