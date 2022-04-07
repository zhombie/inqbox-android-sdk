package kz.inqbox.sdk.socket.logger

import android.util.Log
import kz.inqbox.sdk.socket.Socket

internal object Logger {
    fun debug(tag: String, message: String) {
        if (Socket.isLoggingEnabled()) {
            Log.d(tag, message)
        }
    }

    fun error(tag: String, message: String) {
        if (Socket.isLoggingEnabled()) {
            Log.e(tag, message)
        }
    }

    fun error(tag: String, e: Exception) {
        if (Socket.isLoggingEnabled()) {
            Log.d(tag, e.toString())
        }
    }
}