package kz.qbox.sdk.socket.repository

import kz.qbox.sdk.domain.model.geo.Location

interface SocketLocationRepository {
    fun sendUserLocation(id: String? = null, location: Location)

    fun sendLocationUpdateSubscription()
    fun sendLocationUpdateUnsubscription()
}