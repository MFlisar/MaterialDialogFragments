package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.base.BaseDialogFragment
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.enums.SendResultType
import com.michaelflisar.dialogs.events.DialogInfoEvent
import com.michaelflisar.dialogs.message
import com.michaelflisar.dialogs.neutralButton
import com.michaelflisar.dialogs.setups.DialogInfo
import com.michaelflisar.dialogs.textView
import com.michaelflisar.dialogs.title

open class DialogInfoFragment : BaseDialogFragment() {

    companion object {
        fun create(setup: DialogInfo): DialogInfoFragment {
            val dlg = DialogInfoFragment()
            val args = Bundle().apply {
                putParcelable("setup", setup)
            }
            dlg.arguments = args
            return dlg
        }
    }

    private lateinit var setup: DialogInfo

    private var posButtonText: String? = null
    private var posButton: AppCompatButton? = null
    private var handlerTimer: Handler? = null
    private var timeLeft: Int = 0

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        setup = arguments!!.getParcelable("setup")!!

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("timeLeft")) {
                timeLeft = savedInstanceState.getInt("timeLeft")
            }
        } else {
            if (setup.timerPosButton > 0) {
                timeLeft = setup.timerPosButton
            }
        }

        posButtonText = setup.posButton.get(activity!!)

        if (timeLeft > 0) {
            handlerTimer = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    timeLeft = timeLeft.dec()
                    if (timeLeft > 0) {
                        posButton!!.text = getString(R.string.dialogs_timer_button, posButtonText, timeLeft.toString())
                        handlerTimer!!.postDelayed(this, 1000)
                    } else {
                        posButton!!.isEnabled = true
                        posButton!!.text = posButtonText
                    }
                }
            }
            handlerTimer!!.postDelayed(runnable, 1000)

        }

        val dialog = MaterialDialog(activity!!)
                .cancelable(setup.cancelable)
        this.isCancelable = setup.cancelable

        if (setup.textIsHtml) {
            dialog.customView(R.layout.dialog_webview, scrollable = false)
        } else {
            dialog.message(setup.text)
        }

        dialog.title(setup.title)

        if (posButtonText != null) {
            if (timeLeft > 0) {
                dialog.positiveButton(text = getString(R.string.dialogs_timer_button, posButtonText, timeLeft.toString())) {
                    onClick(WhichButton.POSITIVE)
                }
            } else {
                dialog.positiveButton(text = posButtonText) {
                    onClick(WhichButton.POSITIVE)
                }
            }
        }

        setup.negButton?.let {
            dialog.neutralButton(it) {
                onClick(WhichButton.NEGATIVE)
            }
        }

        setup.neutrButton?.let {
            dialog.neutralButton(it) {
                onClick(WhichButton.NEUTRAL)
            }
        }

        if (setup.warning != null) {
            val content = dialog.textView()!!.getText().toString()
            val contentExtra: String? = setup.warning!!.get(activity!!)
            val spannable = SpannableString(content + setup.warningSeparator + contentExtra)
            spannable.setSpan(
                    ForegroundColorSpan(setup.warningColor),
                    content.length + setup.warningSeparator.length,
                    content.length + setup.warningSeparator.length + contentExtra!!.length,
                    0)
            spannable.setSpan(
                    RelativeSizeSpan(setup.warningTextSizeFactor),
                    content.length + setup.warningSeparator.length,
                    content.length + setup.warningSeparator.length + contentExtra.length,
                    0)
            dialog.textView()!!.setText(spannable)
        }

        if (setup.textIsHtml) {
            var color = ""
            if (setup.darkTheme) {
                color = " color: white;"
            }
            val wv: WebView = dialog.getCustomView().findViewById(R.id.wv)
            var t = ("<html><head>" +
                    "<style type=\"label/css\">"
                    + "body { font-size: 12pt;" + color + " margin: 0px; background-color: transparent; }"
                    + "h1 { margin-left: 0px; font-size: 14pt; label-decoration: underline; font-weight: bold; }"
                    + "h2 { margin-left: 0px; font-size: 13pt; font-weight: bold; }"
                    + "p { font-size: 12pt; }"
                    + "h3 { font-size: 11pt; font-weight: normal;}"
                    + "h4 { font-size: 10pt; font-weight: normal;}"
                    + "code { font-size: 10pt; }"
                    + "li { margin-left: 0px; font-size: 12pt;}"
                    + "ul { padding-left: 30px;}"
                    + "ol { padding-left: 30px;}"
                    + "</style>"
                    + "</head><body>")

            t += setup.text.get(activity!!)
            setup.warning?.let {
                val warningHexColor = Integer.toHexString(setup.warningColor)
                val contentExtra = it.get(activity!!)
                t += "<p><font color=\"#$warningHexColor\">$contentExtra</font></p>"
            }

            t += "</body></html>"

            wv.loadData(t, "label/html; charset=UTF-8", null)
            wv.setBackgroundColor(Color.TRANSPARENT)
        }

        posButton = dialog.getActionButton(WhichButton.POSITIVE)
        if (timeLeft > 0) {
            posButton!!.setEnabled(false)
        }

        return dialog
    }

    private fun onClick(which: WhichButton) {
        sendEvent(DialogInfoEvent(extra, setup.id, which.index))
    }

    override fun onDestroyView() {
        handlerTimer?.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (timeLeft > 0) {
            outState.putInt("timeLeft", timeLeft)
        }
    }

}
