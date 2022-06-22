package kz.qbox.sdk.socket.listener

import kz.qbox.sdk.domain.model.webrtc.IceCandidate
import kz.qbox.sdk.domain.model.webrtc.SessionDescription

interface WebRTCListener {
    fun onCallAccept()
    fun onCallRedirect()
    fun onCallRedial()
    fun onCallPrepare()
    fun onCallReady()
    fun onCallAnswer(sessionDescription: SessionDescription)
    fun onCallOffer(sessionDescription: SessionDescription)
    fun onRemoteIceCandidate(iceCandidate: IceCandidate)
    fun onPeerHangupCall()
}