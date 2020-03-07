package com.michaelflisar.dialogs.adapters

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.setups.DialogList
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.enums.IconSize
import com.michaelflisar.dialogs.getThemeReference
import com.michaelflisar.dialogs.interfaces.ITextImageProvider
import java.util.*

/**
 * Created by Michael on 07.02.2016.
 */
class TextImageRVAdapter(
        private val data: List<ITextImageProvider>,
        grid: Boolean,
        iconSize: IconSize,
        private val noImageVisibility: Int = View.INVISIBLE,
        private val checkMark: Int? = null,
        val mode: DialogList.SelectionMode = DialogList.SelectionMode.None,
        val selection: HashSet<Int> = HashSet(),
        private var onlyShowIconIfSelected: Boolean = false,
        private var hideDefaultCheckMark: Boolean = false,
        private var imageColorFilter: ColorFilter? = null,
        private var imageColorFilterColor: Int? = null,
        private var imageColorFilterMode: PorterDuff.Mode = PorterDuff.Mode.SRC_ATOP,
        private val listener: ((adapter: TextImageRVAdapter, view: TextImageViewHolder, item: ITextImageProvider, pos: Int) -> Unit)?
) : RecyclerView.Adapter<TextImageRVAdapter.TextImageViewHolder>() {

    private val layoutRes: Int = when (iconSize) {
        IconSize.Large -> if (grid) R.layout.row_adapter_text_image_big_grid else R.layout.row_adapter_text_image_big
        IconSize.Medium -> if (grid) R.layout.row_adapter_text_image_medium_grid else R.layout.row_adapter_text_image_medium
        IconSize.Small -> if (grid) R.layout.row_adapter_text_image_grid else R.layout.row_adapter_text_image
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val vh = TextImageViewHolder(inflater.inflate(layoutRes, parent, false))

        if (mode == DialogList.SelectionMode.Multi) {
            if (checkMark == null) {
                if (hideDefaultCheckMark) {
                    vh.text.checkMarkDrawable = null
                } else {
                    vh.text.setCheckMarkDrawable(parent.context.getThemeReference(android.R.attr.listChoiceIndicatorMultiple))
                }
            } else {
                vh.text.setCheckMarkDrawable(checkMark)
            }
        } else if (mode == DialogList.SelectionMode.Single) {
            if (checkMark == null) {
                if (hideDefaultCheckMark) {
                    vh.text.checkMarkDrawable = null
                } else {
                    vh.text.setCheckMarkDrawable(parent.context.getThemeReference(android.R.attr.listChoiceIndicatorSingle))
                }
            } else {
                vh.text.setCheckMarkDrawable(checkMark)
            }
        } else {
            vh.text.checkMarkDrawable = null
        }

        return vh
    }

    override fun onBindViewHolder(holder: TextImageViewHolder, position: Int) {
        if (mode != DialogList.SelectionMode.None) {
            holder.text.isChecked = selection.contains(position)
        } else {
            holder.text.isChecked = false
        }

        if (data[position].subTitle == null) {
            holder.text.text = data[position].title
        } else {
            val styledText = SpannableString(data[position].title + "\n" + data[position].subTitle)
            styledText.setSpan(RelativeSizeSpan(0.7f), data[position].title.length + 1, styledText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.text.text = styledText
        }

        if (data[position].hasImage()) {
            if (onlyShowIconIfSelected) {
                holder.image.visibility = if (holder.text.isChecked) View.VISIBLE else View.INVISIBLE
            } else {
                holder.image.visibility = View.VISIBLE
            }
            data[position].loadImage(holder.image)

            if (imageColorFilter != null && holder.image.drawable != null) {
                holder.image.drawable.colorFilter = imageColorFilter
            }
            if (imageColorFilterColor != null && holder.image.drawable != null) {
                holder.image.drawable.setColorFilter(imageColorFilterColor!!, imageColorFilterMode)
            }
        } else {
            holder.image.visibility = noImageVisibility
        }

        holder.itemView.setOnClickListener {
            listener?.invoke(this@TextImageRVAdapter, holder, data[holder.adapterPosition], holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class TextImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: CheckedTextView
        var image: ImageView

        init {

            text = itemView.findViewById(R.id.text) as CheckedTextView
            image = itemView.findViewById(R.id.image) as ImageView
        }
    }
}
