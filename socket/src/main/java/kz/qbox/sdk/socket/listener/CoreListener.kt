package kz.qbox.sdk.socket.listener

import kz.garage.chat.model.Message

interface CoreListener {
    fun onMessage(message: Message)
}