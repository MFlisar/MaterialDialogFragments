package com.michaelflisar.dialogs.classes

interface DialogLogger {
    fun debug(info: String)
    fun error(exception: Exception)
}