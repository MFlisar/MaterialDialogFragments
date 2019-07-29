package com.michaelflisar.dialogs.adapters

import android.app.Activity
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Filter
import android.widget.ImageView
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.dpToPx
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.getThemeReference
import com.michaelflisar.dialogs.interfaces.ICurrentItemProvider
import com.michaelflisar.dialogs.interfaces.ITextImageProvider
import com.michaelflisar.dialogs.setups.DialogList
import java.util.*

class TextImageAdapter<T : ITextImageProvider>(
        context: Context,
        val items: List<T>,
        private val iconSize: IconSize,
        private val noImageVisibility: Int = View.INVISIBLE,
        private val checkMark: Int? = null,
        val mode: DialogList.SelectionMode = DialogList.SelectionMode.None,
        val selection: HashSet<Int> = HashSet(),
        private var onlyShowIconIfSelected: Boolean = false,
//        private var hideDefaultCheckMark: Boolean = false,
        private var imageColorFilter: ColorFilter? = null,
        private var imageColorFilterColor: Int? = null,
        private var imageColorFilterMode: PorterDuff.Mode = PorterDuff.Mode.SRC_ATOP,
        private val customMargins: Float = 0f

) : HintAdapter<T>(
        context,
        getResource(iconSize),
        items
) {

    companion object {
        fun getResource(iconSize: IconSize): Int {
            return when (iconSize) {
                IconSize.Large -> R.layout.row_adapter_text_image_big
                IconSize.Medium -> R.layout.row_adapter_text_image_medium
                IconSize.Small -> R.layout.row_adapter_text_image
            }
        }
    }

    private var filteredData: List<T>? = null
    private val filter = ItemFilter()

    override fun getCount(): Int {
        return if (filteredData == null) super.getCount() else filteredData!!.size
    }

    override fun getItem(position: Int): T? {
        return if (filteredData == null) super.getItem(position) else filteredData!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setMultiSelect(pos: Int, selected: Boolean) {
        if (!selected) {
            selection.remove(pos)
        } else {
            selection.add(pos)
        }
    }

    fun toggleMultiSelect(pos: Int) {
        if (selection.contains(pos)) {
            selection.remove(pos)
        } else {
            selection.add(pos)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getInternalView(false, position, convertView, parent)

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getInternalView(true, position, convertView, parent)
    }

    @Suppress("NAME_SHADOWING")
    private fun getInternalView(dropdown: Boolean, position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder
        val rowItem = getItem(position)

        val mInflater = parent.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = mInflater.inflate(getResource(iconSize), parent, false)
            holder = ViewHolder()
            holder.text = convertView!!.findViewById(R.id.text)
            holder.image = convertView.findViewById(R.id.image)

            if (mode == DialogList.SelectionMode.Multi) {
                holder.text!!.setCheckMarkDrawable(checkMark
                        ?: parent.context.getThemeReference(android.R.attr.listChoiceIndicatorMultiple))
            } else if (mode == DialogList.SelectionMode.Single) {
                holder.text!!.setCheckMarkDrawable(checkMark
                        ?: parent.context.getThemeReference(android.R.attr.listChoiceIndicatorSingle))
            }

            if (customMargins >= 0f) {
                val padding = parent.context.dpToPx(customMargins)

                val lpImage = holder.image!!.layoutParams as ViewGroup.MarginLayoutParams
                lpImage.setMargins(padding, 0, 0, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    lpImage.marginStart = padding
                }
                holder.image!!.layoutParams = lpImage
                val lpText = holder.text!!.layoutParams as ViewGroup.MarginLayoutParams
                lpText.setMargins(0, 0, padding, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    lpText.marginEnd = padding
                }
                holder.text!!.layoutParams = lpText
            }

            convertView.tag = holder
        } else {
            holder = convertView.tag as TextImageAdapter<T>.ViewHolder
        }

        if (mode != DialogList.SelectionMode.None) {
            holder.text!!.isChecked = selection.contains(position)
        }

        if (rowItem is ICurrentItemProvider) {
            holder.text!!.setTypeface(null, if (dropdown && (rowItem as ICurrentItemProvider).isCurrent) Typeface.BOLD else Typeface.NORMAL)
        }

        if (rowItem!!.subTitle == null) {
            holder.text!!.text = rowItem.title
        } else {
            val styledText = SpannableString(rowItem.title + "\n" + rowItem.subTitle)
            styledText.setSpan(RelativeSizeSpan(0.7f), rowItem.title.length + 1, styledText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.text!!.text = styledText
        }

        if (rowItem.hasImage()) {
            if (onlyShowIconIfSelected) {
                holder.image!!.visibility = if (holder.text!!.isChecked) View.VISIBLE else View.INVISIBLE
            } else {
                holder.image!!.visibility = View.VISIBLE
            }
            rowItem.loadImage(holder.image!!)

            if (imageColorFilter != null && holder.image!!.drawable != null) {
                holder.image!!.colorFilter = imageColorFilter
            }
            if (imageColorFilterColor != null && holder.image!!.drawable != null) {
                holder.image!!.setColorFilter(imageColorFilterColor!!, imageColorFilterMode)
            }
        } else {
            holder.image!!.visibility = noImageVisibility
        }

        return convertView
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()

            val results = Filter.FilterResults()

            val list = items

            val count = list.size
            val nlist = ArrayList<T>(count)

            var filterableString: String?

            for (i in 0 until count) {
                filterableString = list[i].title
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list[i])
                }
                filterableString = list[i].subTitle
                if (filterableString != null && filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list[i])
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filteredData = results.values as ArrayList<T>
            notifyDataSetChanged()
        }

    }

    inner class ViewHolder {
        var image: ImageView? = null
        var text: CheckedTextView? = null
    }

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
                //                iv.setImageDrawable(iv.getContext().getResources().getDrawable(imageId).mutate());
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
}