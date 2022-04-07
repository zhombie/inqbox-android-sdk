package kz.inqbox.sdk.socket.listener

import kz.inqbox.sdk.domain.model.button.RateButton

interface CallListener {
    fun onPendingUsersQueueCount(text: String? = null, count: Int)

    fun onNoOnlineCallAgents(text: String? = null): Boolean

    fun onCallAgentGreet(fullName: String, photoUrl: String? = null, text: String)

    fun onCallFeedback(text: String, rateButtons: List<RateButton>? = null)

    fun onLiveChatTimeout(text: String? = null, timestamp: Long): Boolean
    fun onUserRedirected(text: String? = null, timestamp: Long): Boolean
    fun onCallAgentDisconnected(text: String? = null, timestamp: Long): Boolean
}