package com.michaelflisar.dialogs

import android.content.Context
import android.provider.Settings
import java.security.NoSuchAlgorithmException
import java.util.*


internal object Utils {

    const val DEFAULT_PREF_NAME = "dialogs_ads"
    const val PREF_KEY_SHOW_POLICY_DATE = "show_policy_date"
    const val PREF_KEY_SHOW_POLICY_COUNTER = "show_policy_counter"

    fun getDeviceId(context: Context): String? {
        var deviceId: String? = null
        try {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            deviceId = md5(deviceId).toUpperCase()
        } catch (e: SecurityException) {

        }
        return deviceId
    }

    private fun md5(s: String): String {
        var md5 = ""
        try {
            val digest = java.security.MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuffer()
            for (b in messageDigest) {
                val h = String.format("%02X", b)
                hexString.append(h)
            }
            md5 = hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
        }
        return md5
    }

    fun getLastShowPolicyDate(context: Context, preference: String?): Calendar? {
        val pref = preference ?: DEFAULT_PREF_NAME
        val prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
        val value = prefs.getLong(PREF_KEY_SHOW_POLICY_DATE, -1)
        if (value == -1L) {
            return null
        } else {
            val c = Calendar.getInstance()
            c.timeInMillis = value
            return c
        }
    }

    fun saveLastShowPolicyDate(context: Context, date: Calendar, preference: String?): Boolean {
        val pref = preference ?: DEFAULT_PREF_NAME
        val prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
        return prefs.edit().putLong(PREF_KEY_SHOW_POLICY_DATE, date.timeInMillis).commit()
    }

    fun getLastPolicyCounter(context: Context, preference: String?): Int {
        val pref = preference ?: DEFAULT_PREF_NAME
        val prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
        val value = prefs.getInt(PREF_KEY_SHOW_POLICY_COUNTER, 0)
        prefs.edit().putInt(PREF_KEY_SHOW_POLICY_COUNTER, value).commit()
        return value
    }

    fun setLastPolicyCounter(context: Context, preference: String?, value: Int): Boolean {
        val pref = preference ?: DEFAULT_PREF_NAME
        val prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
        return prefs.edit().putInt(PREF_KEY_SHOW_POLICY_COUNTER, value).commit()
    }
}