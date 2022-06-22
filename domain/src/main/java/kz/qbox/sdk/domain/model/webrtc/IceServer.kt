package kz.qbox.sdk.domain.model.webrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IceServer constructor(
    val url: String,
    val urls: String,
    val username: String? = null,
    val credential: String? = null
) : Parcelable