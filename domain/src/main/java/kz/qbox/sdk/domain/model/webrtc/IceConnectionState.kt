package kz.qbox.sdk.domain.model.webrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class IceConnectionState : Parcelable {
    NEW,
    CHECKING,
    CONNECTED,
    COMPLETED,
    FAILED,
    DISCONNECTED,
    CLOSED;
}