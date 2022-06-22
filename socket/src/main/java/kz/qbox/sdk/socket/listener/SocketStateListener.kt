package kz.qbox.sdk.socket.listener

interface SocketStateListener {
    fun onSocketConnect()
    fun onSocketDisconnect()
}