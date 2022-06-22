package kz.qbox.sdk.socket.listener

import kz.qbox.sdk.domain.model.button.RateButton
import kz.qbox.sdk.socket.model.Greeting

interface CallListener {
    fun onPendingUsersQueueCount(text: String? = null, count: Int)

    fun onNoOnlineCallAgents(text: String? = null): Boolean

    fun onCallAgentGreet(greeting: Greeting)

    fun onCallFeedback(text: String, rateButtons: List<RateButton>? = null)

    fun onLiveChatTimeout(text: String? = null, timestamp: Long): Boolean
    fun onUserRedirected(text: String? = null, timestamp: Long): Boolean
    fun onCallAgentDisconnected(text: String? = null, timestamp: Long): Boolean
}