package kz.inqbox.sdk.socket.event

internal sealed interface Event {

    object Incoming : Event {
//        const val CALL = "call"

        const val CARD102_UPDATE = "card102_update"

        const val CATEGORY_LIST = "category_list"

        const val FEEDBACK = "feedback"

        const val FORM_INIT = "form_init"
        const val FORM_FINAL = "form_final"

        const val LOCATION_UPDATE = "location_update"

        const val MESSAGE = "message"

        const val OPERATOR_GREET = "operator_greet"

        const val OPERATOR_TYPING = "operator_typing"

        const val USER_QUEUE = "user_queue"

        const val TASK_MESSAGE = "task_message"
    }

    object Outgoing : Event {
        const val CANCEL = "cancel"

        const val CANCEL_PENDING_CALL = "cancel_pending_call"

        const val CONFIRM_FUZZY_TASK = "confirm_fuzzy_task"

        const val EXTERNAL = "external"

        const val FORM_INIT = "form_init"
        const val FORM_FINAL = "form_final"

        const val INITIALIZE = "initialize"
        const val REINITIALIZE = "reinitialize"

        const val LOCATION_SUBSCRIBE = "location_subscribe"
        const val LOCATION_UNSUBSCRIBE = "location_unsubscribe"

        const val MESSAGE = "message"

        const val USER_DASHBOARD = "user_dashboard"

        const val USER_FEEDBACK = "user_feedback"

        const val USER_LANGUAGE = "user_language"

        const val USER_LOCATION = "user_location"

        const val USER_MESSAGE = "user_message"
    }

}