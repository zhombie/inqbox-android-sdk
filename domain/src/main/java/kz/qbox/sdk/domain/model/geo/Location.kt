package kz.qbox.sdk.domain.model.geo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location constructor(
    val provider: String? = null,
    val latitude: Double,
    val longitude: Double,
    val bearing: Float? = null,
    val bearingAccuracyDegrees: Float? = null,
    val xAccuracyMeters: Float? = null,
    val yAccuracyMeters: Float? = null,
    val speed: Float? = null,
    val speedAccuracyMetersPerSecond: Float? = null,
    val elapsedRealtimeNanos: Long? = null,
    val elapsedRealtimeUncertaintyNanos: Double? = null
) : Parcelable