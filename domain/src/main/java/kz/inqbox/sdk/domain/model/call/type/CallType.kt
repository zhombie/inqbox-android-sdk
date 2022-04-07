package kz.inqbox.sdk.domain.model.call.type

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CallType constructor(val value: String) : Parcelable {
    TEXT("text"),
    AUDIO("audio"),
    VIDEO("video");

    companion object {
        fun from(value: String): CallType? =
            when (value) {
                TEXT.value -> TEXT
                AUDIO.value -> AUDIO
                VIDEO.value -> VIDEO
                else -> null
            }
    }

    fun isMedia(): Boolean = this == AUDIO || this == VIDEO
}