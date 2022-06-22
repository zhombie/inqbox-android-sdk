package kz.qbox.sdk.socket.listener

import kz.qbox.sdk.socket.model.Card102Status
import kz.qbox.sdk.socket.model.LocationUpdate

interface ARMListener {
    fun onCard102Update(card102Status: Card102Status)
    fun onLocationUpdate(locationUpdates: List<LocationUpdate>)
}