package kz.inqbox.sdk.socket.listener

import kz.inqbox.sdk.socket.model.Card102Status
import kz.inqbox.sdk.socket.model.LocationUpdate

interface ARMListener {
    fun onCard102Update(card102Status: Card102Status)
    fun onLocationUpdate(locationUpdates: List<LocationUpdate>)
}