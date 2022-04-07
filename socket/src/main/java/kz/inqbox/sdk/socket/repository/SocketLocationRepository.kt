package kz.inqbox.sdk.socket.repository

import kz.inqbox.sdk.domain.model.geo.Location

interface SocketLocationRepository {
    fun sendUserLocation(id: String? = null, location: Location)

    fun sendLocationUpdateSubscription()
    fun sendLocationUpdateUnsubscription()
}