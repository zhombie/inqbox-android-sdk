package kz.inqbox.sdk.socket.repository

import kz.garage.multimedia.store.model.Content

interface ChatRepository {
    fun getParentCategories()
    fun getCategories(parentId: Long)
    fun getResponse(id: Long)

    fun sendUserTextMessage(message: String)
    fun sendUserRichMessage(content: Content): Boolean

    fun sendFuzzyTaskConfirmation(name: String, email: String, phone: String)
}