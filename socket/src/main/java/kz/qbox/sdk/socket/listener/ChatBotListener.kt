package kz.qbox.sdk.socket.listener

import kz.qbox.sdk.socket.model.Category

interface ChatBotListener {
    fun onFuzzyTaskOffered(text: String, timestamp: Long): Boolean
    fun onNoResultsFound(text: String, timestamp: Long): Boolean
    fun onCategories(categories: List<Category>)
}