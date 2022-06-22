package kz.qbox.sdk.domain.model.message.call

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CallAction constructor(val value: String) : Parcelable {
    CALL_ACCEPT("call_accept"),
    CALL_REDIAL("call_redial"),
    CALL_REDIRECT("call_redirect"),
    CHAT_TIMEOUT("chat_timeout"),
    FINISH("finish"),
    REDIRECT("redirect"),
    OPERATOR_DISCONNECT("operator_disconnect");

    override fun toString(): String = value
}