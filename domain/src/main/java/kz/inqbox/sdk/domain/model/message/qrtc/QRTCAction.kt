package kz.inqbox.sdk.domain.model.message.qrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class QRTCAction constructor(val value: String) : Parcelable {
    START("start"),
    PREPARE("prepare"),
    READY("ready"),
    OFFER("offer"),
    ANSWER("answer"),
    CANDIDATE("candidate"),
    HANGUP("hangup");

    override fun toString(): String = value
}