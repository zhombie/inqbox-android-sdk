package kz.qbox.sdk.socket.repository

import kz.qbox.sdk.domain.model.language.Language

interface SocketRepository : ListenerRepository,
    EventRegistrationRepository,
    SocketStateRepository,
    FormRepository,
    SocketLocationRepository,
    CallRepository,
    ChatRepository
{
    fun getId(): String?
    fun isConnected(): Boolean

    fun sendUserLanguage(language: Language)

    fun sendExternal(callbackData: String? = null)

    fun sendCancel()
}