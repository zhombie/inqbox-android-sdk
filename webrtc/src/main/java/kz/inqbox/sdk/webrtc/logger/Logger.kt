package kz.inqbox.sdk.webrtc.logger

import android.os.Build
import android.util.Log
import kz.inqbox.sdk.webrtc.WebRTC

internal object Logger {

    private const val LIMIT = 4000

    @JvmStatic
    fun debug(tag: String, message: String) {
        if (WebRTC.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.d(tag, message.substring(0, LIMIT))
                debug(tag, message.substring(LIMIT))
            } else {
                Log.d(tag, message)
            }
        }
    }

    @JvmStatic
    fun error(tag: String, message: String) {
        if (WebRTC.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.e(tag, message.substring(0, LIMIT))
                error(tag, message.substring(LIMIT))
            } else {
                Log.e(tag, message)
            }
        }
    }

    @JvmStatic
    fun warn(tag: String, message: String) {
        if (WebRTC.isLoggingEnabled()) {
            if (message.length > LIMIT) {
                Log.w(tag, message.substring(0, LIMIT))
                warn(tag, message.substring(LIMIT))
            } else {
                Log.w(tag, message)
            }
        }
    }

    fun logDeviceInfo(tag: String) {
        if (WebRTC.isLoggingEnabled()) {
            Log.d(
                tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
                        + "Release: " + Build.VERSION.RELEASE + ", "
                        + "Brand: " + Build.BRAND + ", "
                        + "Device: " + Build.DEVICE + ", "
                        + "Id: " + Build.ID + ", "
                        + "Hardware: " + Build.HARDWARE + ", "
                        + "Manufacturer: " + Build.MANUFACTURER + ", "
                        + "Model: " + Build.MODEL + ", "
                        + "Product: " + Build.PRODUCT
            )
        }
    }

}