package com.michaelflisar.dialogs.helper

import android.os.Handler
import android.os.Looper
import java.util.*

abstract class EventQueue(private val forceMainThread: Boolean) {
    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val queue = LinkedList<Any>()
    private var paused = true

    fun handleEvent(event: Any) {
        if (!paused) {
            if (forceMainThread) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    onEventDeliveration(event)
                } else {
                    mainThreadHandler!!.post { onEventDeliveration(event) }
                }
            } else
                onEventDeliveration(event)
        } else
            queue.add(event)
    }

    fun handleEventNoQueuing(event: Any) {
        if (!paused)
            onEventDeliveration(event)
    }

    fun onResume() {
        paused = false
        while (!queue.isEmpty())
            onEventDeliveration(queue.poll())
    }

    fun onPause() {
        paused = true
    }

    fun onDestroy() {
        mainThreadHandler = null
    }

    abstract fun onEventDeliveration(event: Any)
}