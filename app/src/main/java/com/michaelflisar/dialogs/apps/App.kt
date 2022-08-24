package com.michaelflisar.dialogs.apps

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

class App(
    val name: String,
    val packageName: String,
    val resolveInfo: ResolveInfo?
) {
    constructor(pm: PackageManager, resolveInfo: ResolveInfo) : this(
        resolveInfo.loadLabel(pm)?.toString() ?: "",
        resolveInfo.activityInfo.packageName,
        resolveInfo
    )
}