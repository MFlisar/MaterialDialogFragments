package com.michaelflisar.dialogs.classes

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import androidx.core.text.toSpannable
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.MaterialDialogFragmentUtil
import com.michaelflisar.dialogs.list.R
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultFilter(
    val searchInText: Boolean = true,
    val searchInSubText: Boolean = true,
    val highlight: Boolean = true,
    val algorithm: Algorithm = Algorithm.String,
    val ignoreCase: Boolean = true,
    override val unselectInvisibleItems: Boolean = true
) : DialogList.Filter {

    private val regexSplitWords = "\\s+".toRegex()

    override fun matches(context: Context, item: DialogList.ListItem, filter: String): Boolean {
        if (filter.isEmpty())
            return true
        return when (algorithm) {
            Algorithm.String -> matchesWord(context, item, filter)
            Algorithm.Words -> {
                // we highlight all words that we can find
                val words = filter.split(regexSplitWords)
                var matches = false
                for (word in words) {
                    if (matchesWord(context, item, word)) {
                        matches = true
                        break
                    }
                }
                matches
            }
        }
    }

    override fun displayText(
        tv: TextView,
        item: DialogList.ListItem,
        filter: String
    ): CharSequence {
        return if (filter.isEmpty() || !searchInText || !highlight)
            item.text.display(tv)
        else {
            val text = getHighlightedText(tv.context, item.text, filter)
            tv.text = text
            text
        }
    }

    override fun displaySubText(
        tv: TextView,
        item: DialogList.ListItem,
        filter: String
    ): CharSequence {
        return if (filter.isEmpty() || !searchInSubText || !highlight)
            item.subText.display(tv)
        else {
            val text = getHighlightedText(tv.context, item.subText, filter)
            tv.text = text
            text
        }
    }

    private fun matchesWord(context: Context, item: DialogList.ListItem, word: String): Boolean {
        return if (searchInText && item.text.get(context).toString().contains(word, ignoreCase))
            true
        else if (searchInSubText && item.subText.get(context).toString().contains(word, ignoreCase))
            true
        else
            false
    }

    private fun getHighlightedText(context: Context, text: Text, filter: String): Spannable {
        var spannable = SpannableString(text.get(context)).toSpannable()
        return when (algorithm) {
            Algorithm.String -> {
                // we highlight the full search term
                highlightText(context, spannable, filter)
            }
            Algorithm.Words -> {
                // we highlight all words that we can find
                val words = filter.split(regexSplitWords)
                for (word in words)
                    spannable = highlightText(context, spannable, word)
                spannable
            }
        }
    }

    private fun highlightText(context: Context, spannable: Spannable, text: String): Spannable {
        var ofe: Int = spannable.indexOf(text, 0, ignoreCase)
        var ofs = 0
        while (ofs < spannable.length && ofe != -1) {
            ofe = spannable.indexOf(text, ofs, ignoreCase)
            if (ofe == -1) break else {
                // bold span
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    ofe,
                    ofe + text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // color span
                spannable.setSpan(
                    ForegroundColorSpan(MaterialDialogFragmentUtil.getThemeColorAttr(context, R.attr.colorAccent)),
                    ofe,
                    ofe + text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            ofs = ofe + 1
        }
        return spannable
    }

    enum class Algorithm {
        String,
        Words
    }

}