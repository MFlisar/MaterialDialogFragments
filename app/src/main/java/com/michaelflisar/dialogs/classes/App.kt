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
        fun load(context: Context): ArrayList<App> {
            val apps = ArrayList<App>()
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val infos = context.packageManager.queryIntentActivities(mainIntent, 0)
            for (info in infos) {
                apps.add(App(pm, info))
            }

            apps.sortWith { o1, o2 -> o1!!.name.compareTo(o2!!.name, true) }

            return apps
        }
    }

    constructor(pm: PackageManager, resolveInfo: ResolveInfo) : this(
            resolveInfo.loadLabel(pm)?.toString() ?: "",
            resolveInfo.activityInfo.packageName,
            resolveInfo
    )
}