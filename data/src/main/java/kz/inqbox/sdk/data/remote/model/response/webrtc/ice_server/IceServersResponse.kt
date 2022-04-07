package kz.inqbox.sdk.data.remote.model.response.webrtc.ice_server

import com.google.gson.annotations.SerializedName
import kz.inqbox.sdk.data.remote.model.response.webrtc.ice_server.IceServerResponse

data class IceServersResponse constructor(
    @SerializedName("ice_servers")
    val iceServers: List<IceServerResponse>?
)