package kz.inqbox.sdk.data.remote.model.response.geo

import kz.inqbox.sdk.domain.model.geo.Location

fun LocationResponse.toLocation(): Location =
    Location(
        provider = provider,
        latitude = latitude,
        longitude = longitude,
        bearing = bearing,
        bearingAccuracyDegrees = bearingAccuracyDegrees,
        xAccuracyMeters = xAccuracyMeters,
        yAccuracyMeters = yAccuracyMeters,
        speed = speed,
        speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond,
        elapsedRealtimeNanos = elapsedRealtimeNanos,
        elapsedRealtimeUncertaintyNanos = elapsedRealtimeUncertaintyNanos
    )
