package kz.qbox.sdk.socket.listener

import kz.garage.chat.model.Message
import kz.qbox.sdk.domain.model.form.Form

interface FormListener {
    fun onFormInit(form: Form)
    fun onFormFound(message: Message, form: Form): Boolean
    fun onFormFinal(trackId: String?, taskId: Long?, message: String?, success: Boolean)
}