package com.michaelflisar.dialogs.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


object KeyboardUtils {
    fun hideKeyboard(parent: Context?, currentFocus: View?) {
        if (parent != null && currentFocus != null) {
            val imm = parent.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            // imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    fun hideKeyboardWithZeroFlag(parent: Context?, currentFocus: View?) {
        if (parent != null && currentFocus != null) {
            val imm = parent.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0)
        }
    }


    fun showKeyboard(parent: Context?, currentFocus: View?) {
        if (parent != null && currentFocus != null) {
            val imm = parent.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.SHOW_FORCED)
            // imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    fun showKeyboard(parent: Context) {
        val imm = parent.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}