package kz.qbox.sdk.webrtc.utils

internal object ThreadUtils {

    @JvmStatic
    val currentThread: String
        get() {
            val thread = Thread.currentThread()
            return "Thread[id=${thread.id}, name=${thread.name}]"
        }

}