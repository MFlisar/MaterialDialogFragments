package com.michaelflisar.dialogs.debug

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.michaelflisar.dialogs.core.BuildConfig
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.michaelflisar.dialogs.textView

object DebugDialog {

    // --------------
    // Einstellungen
    // --------------

    private val DEFAULT_PREF_NAME = "debug_settings"
    private lateinit var cache: SharedPrefsCache

    fun init(context: Context, customPrefName: String? = null) {
        cache = SharedPrefsCache(context.getSharedPreferences(customPrefName
                ?: DEFAULT_PREF_NAME, Context.MODE_PRIVATE))
    }

    fun findEntry(prefName: String, items: List<DebugDialog.Entry<*>>): DebugDialog.Entry<*>? {

        for (e in items) {
            val isEntry = (e is EntryWithPref<*>) && e.prefName.equals(prefName)
            if (isEntry) {
                return e
            }
            if (e is DebugDialog.SubEntryHolder<*, *>) {
                val entry = findEntry(prefName, e.subEntries)
                if (entry != null) {
                    return entry
                }
            }
        }

        return null
    }

    fun reset(items: List<DebugDialog.Entry<*>>) {

        for (e in items) {
            if (e is EntryWithPref<*>) {
                e.reset()
            }
            if (e is DebugDialog.SubEntryHolder<*, *>) {
                reset(e.subEntries)
            }
        }
    }

    // ------------
    // Dialog
    // ------------

    fun showDialog(items: List<Entry<*>>, activity: FragmentActivity, darkTheme: Boolean, backButtonText: String, withNumbering: Boolean, customTitle: String? = null) {

        val visibleItems = items.filter { BuildConfig.DEBUG || it.visibleInRelease }

        val dialog = MaterialDialog(activity)
        val adapter = DebugAdapter(visibleItems, activity, dialog, darkTheme, withNumbering)
        dialog
                .noAutoDismiss()
                .title(text = customTitle ?: "Debug Menu")
                .message(text = "")
                .negativeButton(text = backButtonText) {
                    if (!adapter.goLevelUp()) {
                        it.dismiss()
                    }
                }
                .customListAdapter(adapter)
                .show()

        updateSubTitle(dialog, null, null)
    }

    internal fun updateSubTitle(dialog: MaterialDialog, parentEntry: Entry<*>?, number: String?) {
        if (parentEntry?.name != null) {
            dialog.textView()?.text = (number?.plus(" ") ?: "") + parentEntry.name
            dialog.textView()?.visibility = View.VISIBLE
        } else {
            dialog.textView()?.visibility = View.GONE
        }
    }

    // ------------
    // Classes
    // ------------

    interface SubEntryHolder<S : Entry<*>, Parent : Entry<*>> {
        var subEntries: ArrayList<S>

        fun subEntries(block: (Parent) -> ArrayList<S>): Parent {
            @Suppress("UNCHECKED_CAST")
            subEntries = block(this as Parent)
            return this
        }
    }

    interface EntryWithPref<T> {
        val prefName: String
        val defaultValue: T

        fun reset()
    }

    sealed class Entry<T>(val name: String) {

        internal var visibleInRelease = true

        class Group(name: String, override var subEntries: ArrayList<Entry<*>> = arrayListOf()) : Entry<Unit>(name), SubEntryHolder<Entry<*>, Group>
        class Button(name: String, val function: () -> Unit) : Entry<Unit>(name) {
            override fun onClick(): Array<ClickResult> {
                function()
                return emptyArray()
            }
        }

        class Checkbox(name: String, override val prefName: String, override val defaultValue: Boolean) : Entry<Boolean>(name), EntryWithPref<Boolean> {
            override fun reset() {
                setBool(this, defaultValue)
            }

            override fun onClick(): Array<ClickResult> {
                setBool(this, !getBool(this))
                return arrayOf(ClickResult.Notify)
            }
        }

        class List(name: String, override val prefName: String, override val defaultValue: Int, override var subEntries: ArrayList<ListEntry> = arrayListOf()) : Entry<Int>(name), EntryWithPref<Int>, SubEntryHolder<ListEntry, List> {
            override fun reset() {
                setInt(this, defaultValue)
            }

            fun getEntryByValue(value: Int) = subEntries.find { it.value == value }!!
        }

        class ListEntry(name: String, val parent: List, val value: Int) : Entry<Int>(name) {
            override fun onClick(): Array<ClickResult> {
                setInt(parent, value)
                return arrayOf(ClickResult.GoUp)
            }
        }

        open fun onClick(): Array<ClickResult> {
            return emptyArray()
        }

        fun withVisibleInRelease(visible: Boolean) : Entry<T> {
            this.visibleInRelease = visible
            return this
        }
    }

    enum class ClickResult {
        Notify,
        GoUp
    }

    // --------------
    // SharedPrefsCache + Funktionen
    // --------------

    fun getBool(entry: Entry.Checkbox) = cache.getBoolean(entry.prefName, entry.defaultValue)
    fun setBool(entry: Entry.Checkbox, enabled: Boolean) = cache.putBoolean(entry.prefName, enabled)

    fun getInt(entry: Entry.List) = cache.getInt(entry.prefName, entry.defaultValue)
    fun setInt(entry: Entry.List, value: Int) = cache.putInt(entry.prefName, value)

    internal class SharedPrefsCache(private val sharedPreferences: SharedPreferences) {

        val map = hashMapOf<String, Any>()

        internal fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            if (map.containsKey(key)) {
                return map.get(key) as Boolean
            }

            val value = sharedPreferences.getBoolean(key, defaultValue)
            map.put(key, value)
            return value
        }

        internal fun putBoolean(key: String, value: Boolean) {
            map.put(key, value)
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        internal fun getInt(key: String, defaultValue: Int): Int {
            if (map.containsKey(key)) {
                return map.get(key) as Int
            }

            val value = sharedPreferences.getInt(key, defaultValue)
            map.put(key, value)
            return value
        }

        internal fun putInt(key: String, value: Int) {
            map.put(key, value)
            sharedPreferences.edit().putInt(key, value).apply()
        }
    }
}