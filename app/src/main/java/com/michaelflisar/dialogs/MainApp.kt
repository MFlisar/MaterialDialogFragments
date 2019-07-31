package com.michaelflisar.dialogs

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class MainApp : Application() {

    init {

        // should be done ONCE in an application only
        // optionally we can enable cancel events, but then we must handle them in onDialogResultAvailable as well
        // this defines the default value => EVERY SETUP allows to overwrite this default value for a single dialog!
        // by default, those events are not enabled!!!

        // we enable those cancel events to show toasts in our example activity
        DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT = true
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}