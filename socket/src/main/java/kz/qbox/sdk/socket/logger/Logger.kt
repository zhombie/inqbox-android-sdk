package kz.qbox.sdk.socket.logger

import android.util.Log
import kz.qbox.sdk.socket.Socket

internal object Logger {

    private const val LIMIT = 4000

    fun debug(tag: String, message: String) {
        if (Socket.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.d(tag, message.substring(0, LIMIT))
                debug(tag, message.substring(LIMIT))
            } else {
                Log.d(tag, message)
            }
        }
    }

    fun error(tag: String, message: String) {
        if (Socket.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.e(tag, message.substring(0, LIMIT))
                error(tag, message.substring(LIMIT))
            } else {
                Log.e(tag, message)
            }
        }
    }

    fun error(tag: String, e: Exception) {
        if (Socket.isLoggingEnabled()) {
            Log.d(tag, e.toString())
        }
    }

    fun warn(tag: String, message: String) {
        if (Socket.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.w(tag, message.substring(0, LIMIT))
                warn(tag, message.substring(LIMIT))
            } else {
                Log.w(tag, message)
            }
        }
    }

}