package kz.inqbox.sdk.socket.listener

interface SocketStateListener {
    fun onSocketConnect()
    fun onSocketDisconnect()
}