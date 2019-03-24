package com.michaelflisar.dialogs.interfaces

import android.widget.ImageView

interface ITextImageProvider {
    val title: String
    val subTitle: String?
    fun loadImage(iv: ImageView)
    fun hasImage(): Boolean
}