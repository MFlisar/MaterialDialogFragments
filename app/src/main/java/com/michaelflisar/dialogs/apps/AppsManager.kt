package com.michaelflisar.dialogs.apps

import android.content.Context
import android.content.Intent

object AppsManager {

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