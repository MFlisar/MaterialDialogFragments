package com.michaelflisar.dialogs.classes

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

class App(
    val name: String,
    val packageName: String,
    val resolveInfo: ResolveInfo?
) {
    companion object {

        private var apps: ArrayList<App>? = null

        fun load(context: Context): ArrayList<App> {
            resetCache()
            return loadAndCache(context)
        }

        fun loadAndCache(context: Context): ArrayList<App> {
            if (apps != null) {
                return apps!!
            }

            apps = ArrayList()

            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val infos = context.packageManager.queryIntentActivities(mainIntent, 0)
            for (info in infos) {
                apps!!.add(App(pm, info))
            }

            apps!!.sortWith(object : Comparator<App> {
                override fun compare(o1: App?, o2: App?) = o1!!.name.compareTo(o2!!.name, true)
            })

            return apps!!
        }

        fun resetCache() {
            apps = null
        }
    }

    constructor(pm: PackageManager, resolveInfo: ResolveInfo) : this(
        resolveInfo.loadLabel(pm)?.toString() ?: "",
        resolveInfo.activityInfo.packageName,
        resolveInfo
    )
}