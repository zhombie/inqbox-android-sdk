package kz.inqbox.sdk.webrtc.mapper

import kz.inqbox.sdk.domain.model.webrtc.AdapterType
import kz.inqbox.sdk.domain.model.webrtc.IceCandidate

internal class IceCandidateMapper {

    companion object {
        fun map(iceCandidate: org.webrtc.IceCandidate, adapterType: AdapterType): IceCandidate {
            return IceCandidate(
                sdpMid = iceCandidate.sdpMid,
                sdpMLineIndex = iceCandidate.sdpMLineIndex,
                sdp = iceCandidate.sdp,
                serverUrl = iceCandidate.serverUrl,
                adapterType = adapterType
            )
        }

        fun map(iceCandidate: IceCandidate): org.webrtc.IceCandidate {
            return org.webrtc.IceCandidate(
                iceCandidate.sdpMid,
                iceCandidate.sdpMLineIndex,
                iceCandidate.sdp
            )
        }
    }

}