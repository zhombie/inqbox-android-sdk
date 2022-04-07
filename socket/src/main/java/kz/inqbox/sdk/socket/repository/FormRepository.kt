package kz.inqbox.sdk.socket.repository

import kz.inqbox.sdk.domain.model.form.Form
import kz.inqbox.sdk.socket.model.Sender

interface FormRepository {
    fun sendFormInitialize(formId: Long)
    fun sendFormFinalize(sender: Sender?, form: Form, extraFields: List<Form.Field>)
}