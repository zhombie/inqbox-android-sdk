package kz.qbox.sdk.data.remote.model.response.webrtc.ice_server

import kz.qbox.sdk.domain.model.webrtc.IceServer

fun IceServerResponse.toIceServer(): IceServer =
    IceServer(
        url = url ?: "",
        urls = urls ?: "",
        username = username,
        credential = credential
    )


fun IceServersResponse.toIceServers(): List<IceServer>? =
    iceServers?.map { it.toIceServer() }