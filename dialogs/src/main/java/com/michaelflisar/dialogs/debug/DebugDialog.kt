package com.michaelflisar.dialogs.debug

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getListAdapter
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.core.BuildConfig
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

    fun findEntry(prefName: String, items: List<Entry<*>>): Entry<*>? {

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

    fun reset(items: List<Entry<*>>, dialog: Dialog?) {

        for (e in items) {
            if (e is EntryWithPref<*>) {
                e.reset()
            }
            if (e is DebugDialog.SubEntryHolder<*, *>) {
                reset(e.subEntries, null)
            }
        }

        (dialog as? MaterialDialog)?.let {
            val adapter = it.getListAdapter() as DebugAdapter
            adapter.notifyDataSetChanged()
        }
    }

    fun deleteAll() = cache.deleteAll()

    fun delete(items: List<Entry<*>>){
        val keys = getAllKeys(items)
        cache.delete(keys)
    }

    fun deleteDeprecated(itemsToKeep: List<DebugDialog.Entry<*>>) {
        val keys = getAllKeys(itemsToKeep)
        cache.deleteDeprecated(keys)
    }

    private fun getAllKeys(items: List<Entry<*>>): ArrayList<String> {
        val keys = ArrayList<String>()
        for (e in items) {
            if (e is EntryWithPref<*>) {
                keys.add(e.prefName)
            }
            if (e is SubEntryHolder<*, *>) {
                keys.addAll(getAllKeys(e.subEntries))
            }
        }
        return keys
    }

    // ------------
    // Dialog
    // ------------

    fun showDialog(items: List<Entry<*>>, activity: FragmentActivity, backButtonText: String, withNumbering: Boolean, isDebug: Boolean, customTitle: String? = null) {

        val visibleItems = items.filter { isDebug || it.visibleInRelease }

        val dialog = MaterialDialog(activity)
        val adapter = DebugAdapter(visibleItems, activity, dialog, withNumbering)
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
        class Button(name: String, val function: (dialog: Dialog) -> Unit) : Entry<Unit>(name) {
            override fun onClick(dialog: Dialog): Array<ClickResult> {
                function(dialog)
                return emptyArray()
            }
        }

        class Checkbox(name: String, override val prefName: String, override val defaultValue: Boolean, val sideEffect: ((value: Boolean) -> Unit)? = null) : Entry<Boolean>(name), EntryWithPref<Boolean> {
            override fun reset() {
                setBool(this, defaultValue)
            }

            override fun onClick(dialog: Dialog): Array<ClickResult> {
                val newValue = !getBool(this)
                setBool(this, newValue)
                sideEffect?.invoke(newValue)
                return arrayOf(ClickResult.Notify)
            }
        }

        class List(name: String, override val prefName: String, override val defaultValue: Int, override var subEntries: ArrayList<ListEntry> = arrayListOf(), val sideEffect: ((value: Int) -> Unit)? = null) : Entry<Int>(name), EntryWithPref<Int>, SubEntryHolder<ListEntry, List> {
            override fun reset() {
                setInt(this, defaultValue)
            }

            fun getEntryByValue(value: Int) = subEntries.find { it.value == value }!!

            fun addEntries(vararg items: ListEntry) {
                subEntries.addAll(items)
            }
        }

        class ListEntry(name: String, val parent: List, val value: Int) : Entry<Int>(name) {
            override fun onClick(dialog: Dialog): Array<ClickResult> {
                setInt(parent, value)
                parent.sideEffect?.invoke(value)
                return arrayOf(ClickResult.GoUp)
            }
        }

        open fun onClick(dialog: Dialog): Array<ClickResult> {
            return emptyArray()
        }

        fun withVisibleInRelease(visible: Boolean): Entry<T> {
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
            val cached = map.get(key)
            if (cached != null && cached is Boolean) {
                return cached
            }
            val value = sharedPreferences.getBoolean(key, defaultValue)
            map[key] = value
            return value
        }

        internal fun putBoolean(key: String, value: Boolean) {
            map[key] = value
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        internal fun getInt(key: String, defaultValue: Int): Int {
            val cached = map.get(key)
            if (cached != null && cached is Int) {
                return cached
            }
            val value = sharedPreferences.getInt(key, defaultValue)
            map[key] = value
            return value
        }

        internal fun putInt(key: String, value: Int) {
            map[key] = value
            sharedPreferences.edit().putInt(key, value).apply()
        }

        internal fun delete(keys: List<String>) {
            for (k in keys) {
                map.remove(k)
            }
            val editor = sharedPreferences.edit()
            for (k in keys) {
                editor.remove(k)
            }
            editor.apply()
        }

        internal fun deleteDeprecated(keysToKeep: List<String>) {
            val keysToDelete = ArrayList<String>()
            val allKeys = sharedPreferences.all.keys
            for (k in allKeys) {
                if (!keysToKeep.contains(k))
                    keysToDelete.add(k)
            }
            val editor = sharedPreferences.edit()
            for (k in keysToDelete) {
                editor.remove(k)
            }
            editor.apply()
        }

        internal fun deleteAll() {
            map.clear()
            sharedPreferences.edit().clear().apply()
        }
    }
}