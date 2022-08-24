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
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.setups.DialogInfo

open class DialogInfoFragment : MaterialDialogFragment<DialogInfo>() {

    companion object {
        fun create(setup: DialogInfo): DialogInfoFragment {
            val dlg = DialogInfoFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }

        const val PLACEHOLDER_BODY_STYLE_EXTRA = "##BODY_EXTRA_STYLES##"
        const val PLACEHOLDER_BODY = "##BODY##"

        const val HTML_BASE = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "body { font-size: 18pt; margin: 0px; background-color: transparent; $PLACEHOLDER_BODY_STYLE_EXTRA}\n" +
                "h1 { margin-left: 0px; font-size: 14pt; label-decoration: underline; font-weight: bold; }\n" +
                "h2 { margin-left: 0px; font-size: 13pt; font-weight: bold; }\n" +
                "h3 { font-size: 11pt; font-weight: normal;}\n" +
                "h4 { font-size: 10pt; font-weight: normal;}\n" +
                "p { font-size: 12pt; }\n" +
                "code { font-size: 10pt; }\n" +
                "li { margin-left: 0px; font-size: 12pt;}\n" +
                "ul { padding-left: 30px;}\n" +
                "ol { padding-left: 30px;}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>$PLACEHOLDER_BODY</body>\n" +
                "</html>"
    }

    private var posButtonText: String? = null
    private var posButton: AppCompatButton? = null
    private var handlerTimer: Handler? = null
    private var timeLeft: Int = 0

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("timeLeft")) {
                timeLeft = savedInstanceState.getInt("timeLeft")
            }
        } else {
            if (setup.timerPosButton > 0) {
                timeLeft = setup.timerPosButton
            }
        }

        posButtonText = setup.posButton.get(requireActivity())

        if (timeLeft > 0) {
            handlerTimer = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    timeLeft = timeLeft.dec()
                    if (timeLeft > 0) {
                        posButton!!.text = getString(R.string.mdf_dialogs_timer_button, posButtonText, timeLeft.toString())
                        handlerTimer!!.postDelayed(this, 1000)
                    } else {
                        posButton!!.isEnabled = true
                        posButton!!.text = posButtonText
                    }
                }
            }
            handlerTimer!!.postDelayed(runnable, 1000)

        }

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(requireActivity(), this)

        if (setup.textIsHtml) {
            dialog.customView(R.layout.dialog_webview, scrollable = false)
        } else {
            dialog.message(setup.text)
        }

        if (posButtonText != null) {
            if (timeLeft > 0) {
                dialog.positiveButton(text = getString(R.string.mdf_dialogs_timer_button, posButtonText, timeLeft.toString())) {
                    onClick(WhichButton.POSITIVE)
                }
            } else {
                dialog.positiveButton(text = posButtonText) {
                    onClick(WhichButton.POSITIVE)
                }
            }
        }

        dialog
                .negativeButton(setup) {
                    onClick(WhichButton.NEGATIVE)
                    dismiss()
                }
                .neutralButton(setup) {
                    onClick(WhichButton.NEUTRAL)
                }


        if (!setup.textIsHtml && setup.warning != null) {
            val content = dialog.textView()!!.text.toString()
            val contentExtra = setup.warning!!.get(requireActivity())
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
            dialog.textView()!!.text = spannable
        }

        if (setup.textIsHtml) {
            var color = ""
            if (DialogSetup.isUsingDarkTheme(requireActivity())) {
                color = " color: white;"
            }
            val wv: WebView = dialog.getCustomView().findViewById(R.id.wv)

            var body = setup.text.get(requireActivity())
            setup.warning?.let {
                val warningHexColor = Integer.toHexString(setup.warningColor)
                val contentExtra = it.get(requireActivity())
                body += "<p><font color=\"#$warningHexColor\">$contentExtra</font></p>"
            }

            val html = HTML_BASE
                    .replace(PLACEHOLDER_BODY_STYLE_EXTRA, color)
                    .replace(PLACEHOLDER_BODY, body)

            wv.loadDataWithBaseURL(null, html, "text/html; charset=UTF-8", "UTF-8", null)
//            wv.loadData(t, "label/html; charset=UTF-8", "UTF-8")
            wv.setBackgroundColor(Color.TRANSPARENT)
        }

        posButton = dialog.getActionButton(WhichButton.POSITIVE)
        if (timeLeft > 0) {
            posButton!!.isEnabled = false
        }

        return dialog
    }

    private fun onClick(which: WhichButton) {
        sendEvent(DialogInfo.Event(setup, MaterialDialogButton.find(which.index)))
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
