package kz.inqbox.sdk.domain.model.webrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IceCandidate constructor(
    val sdpMid: String,
    val sdpMLineIndex: Int,
    val sdp: String,
    val serverUrl: String? = null,
    val adapterType: AdapterType = AdapterType.UNKNOWN,
) : Parcelable {

    companion object {
        val NO_SERVER_URL = null
    }

}