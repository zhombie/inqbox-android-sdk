package kz.inqbox.sdk.webrtc

object WebRTC {

    private var isLoggingEnabled: Boolean = false

    fun init(isLoggingEnabled: Boolean): Boolean {
        return setLoggingEnabled(isLoggingEnabled)
    }

    fun isLoggingEnabled(): Boolean = isLoggingEnabled

    fun setLoggingEnabled(isLoggingEnabled: Boolean): Boolean {
        this.isLoggingEnabled = isLoggingEnabled
        return this.isLoggingEnabled == isLoggingEnabled
    }

}