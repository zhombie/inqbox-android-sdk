package kz.inqbox.sdk.socket.repository

interface EventRegistrationRepository {
    fun registerAllEventListeners(): Boolean
    fun unregisterAllEventListeners(): Boolean

    fun registerSocketConnectEventListener(): Boolean
    fun unregisterSocketConnectEventListener(): Boolean

    fun registerMessageEventListener(): Boolean
    fun unregisterMessageEventListener(): Boolean

    fun registerChatBotDashboardEventListener(): Boolean
    fun unregisterChatBotDashboardEventListener(): Boolean

    fun registerUsersQueueEventListener(): Boolean
    fun unregisterUsersQueueEventListener(): Boolean

    fun registerCallAgentGreetEventListener(): Boolean
    fun unregisterCallAgentGreetEventListener(): Boolean

    fun registerCallAgentTypingEventListener(): Boolean
    fun unregisterCallAgentTypingEventListener(): Boolean

    fun registerCard102UpdateEventListener(): Boolean
    fun unregisterCard102UpdateEventListener(): Boolean

    fun registerLocationUpdateEventListener(): Boolean
    fun unregisterLocationUpdateEventListener(): Boolean

    fun registerUserCallFeedbackEventListener(): Boolean
    fun unregisterUserCallFeedbackEventListener(): Boolean

    fun registerFormInitializeEventListener(): Boolean
    fun unregisterFormInitializeEventListener(): Boolean

    fun registerFormFinalizeEventListener(): Boolean
    fun unregisterFormFinalizeEventListener(): Boolean

    fun registerTaskMessageEventListener(): Boolean
    fun unregisterTaskMessageEventListener(): Boolean

    fun registerSocketDisconnectEventListener(): Boolean
    fun unregisterSocketDisconnectEventListener(): Boolean
}