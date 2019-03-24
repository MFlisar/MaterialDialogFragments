package com.michaelflisar.dialogs.adapters

import android.widget.ImageView
import com.michaelflisar.dialogs.interfaces.ICurrentItemProvider
import com.michaelflisar.dialogs.interfaces.ITextImageProvider

class TextImageItem : ITextImageProvider, ICurrentItemProvider {
    private var imageId: Int = 0
    private var imageLoader: ((ImageView) -> Unit)? = null
    override lateinit var title: String
    override var subTitle: String? = null
    override var isCurrent: Boolean = false

    constructor() {}

    constructor(imageId: Int, title: String, subTitle: String) {
        this.imageId = imageId
        this.imageLoader = null
        this.title = title
        this.subTitle = subTitle
        isCurrent = false
    }

    constructor(imageId: Int, title: String) {
        this.imageId = imageId
        this.imageLoader = null
        this.title = title
        this.subTitle = null
        isCurrent = false
    }

    constructor(imageLoader: (ImageView) -> Unit, title: String, subTitle: String) {
        this.imageId = -1
        this.imageLoader = imageLoader
        this.title = title
        this.subTitle = subTitle
        isCurrent = false
    }

    constructor(imageLoader: (ImageView) -> Unit, title: String) {
        this.imageId = -1
        this.imageLoader = imageLoader
        this.title = title
        this.subTitle = null
        isCurrent = false
    }

    fun setImageLoader(imageLoader: (ImageView) -> Unit) {
        this.imageLoader = imageLoader
    }

    fun setImageId(imageId: Int) {
        this.imageId = imageId
    }

    override fun loadImage(iv: ImageView) {
        if (imageId > 0) {
            iv.setImageResource(imageId)
        } else if (imageLoader != null) {
            imageLoader!!(iv)
        } else {
            iv.setImageDrawable(null)
        }
    }

    override fun hasImage(): Boolean {
        return imageId > 0 || imageLoader != null
    }
}