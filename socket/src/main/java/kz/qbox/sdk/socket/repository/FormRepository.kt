package kz.qbox.sdk.socket.repository

import kz.qbox.sdk.domain.model.form.Form
import kz.qbox.sdk.socket.model.Sender

interface FormRepository {
    fun sendFormInitialize(formId: Long)
    fun sendFormFinalize(sender: Sender?, form: Form, extraFields: List<Form.Field>)
}