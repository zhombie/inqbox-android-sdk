package kz.inqbox.sdk.socket.model

data class LocationUpdate constructor(
    val gpsCode: Long,
    val x: Double,
    val y: Double
)