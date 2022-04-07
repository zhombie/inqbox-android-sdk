package kz.inqbox.sdk.socket.model

enum class Card102Status constructor(val status: Int) {
    NEW_CARD102(1),
    ASSIGNED_FORCE(2),
    FORCE_ON_SPOT(3),
    COMPLETED_OPERATION(4);

    companion object {
        operator fun invoke(status: Int) = values().find { it.status == status }
    }
}