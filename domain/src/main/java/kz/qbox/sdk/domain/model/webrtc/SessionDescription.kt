package kz.qbox.sdk.domain.model.webrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionDescription constructor(
    val type: Type,
    val description: String
) : Parcelable {

    @Parcelize
    enum class Type : Parcelable {
        OFFER,
        ANSWER
    }

}