package kz.inqbox.sdk.webrtc.core.constraints

import org.webrtc.MediaConstraints

interface RTCConstraint<in T> {
    val constraint: String

    fun toKeyValuePair(value: T): MediaConstraints.KeyValuePair =
        MediaConstraints.KeyValuePair(constraint, value.toString())
}