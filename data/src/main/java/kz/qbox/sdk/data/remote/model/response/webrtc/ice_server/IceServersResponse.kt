package kz.qbox.sdk.data.remote.model.response.webrtc.ice_server

import com.google.gson.annotations.SerializedName

data class IceServersResponse constructor(
    @SerializedName("ice_servers")
    val iceServers: List<IceServerResponse>?
)