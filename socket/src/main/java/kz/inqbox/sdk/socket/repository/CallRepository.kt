package kz.inqbox.sdk.socket.repository

import kz.inqbox.sdk.domain.model.message.call.CallAction
import kz.inqbox.sdk.domain.model.message.qrtc.QRTCAction
import kz.inqbox.sdk.domain.model.webrtc.IceCandidate
import kz.inqbox.sdk.domain.model.webrtc.SessionDescription
import kz.inqbox.sdk.socket.model.CallInitialization

interface CallRepository {
    fun sendCallInitialization(callInitialization: CallInitialization)
    fun sendCallReinitialization(callInitialization: CallInitialization)

    fun sendPendingCallCancellation()

    fun sendCallAction(action: CallAction)
    fun sendQRTCAction(action: QRTCAction)
    fun sendLocalSessionDescription(sessionDescription: SessionDescription)
    fun sendLocalIceCandidate(iceCandidate: IceCandidate)

    fun sendUserCallFeedback(chatId: Long, rating: Int)
}