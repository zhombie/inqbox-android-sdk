package kz.qbox.sdk.data.remote.model.response.form.field.type

import kz.qbox.sdk.data.remote.model.response.form.FormResponse
import kz.qbox.sdk.domain.model.form.Form

fun FormResponse.FieldResponse.TypeResponse.toFormFieldType(): Form.Field.Type =
    when (this) {
        FormResponse.FieldResponse.TypeResponse.TEXT -> Form.Field.Type.TEXT

        FormResponse.FieldResponse.TypeResponse.SELECT -> Form.Field.Type.SELECT

        FormResponse.FieldResponse.TypeResponse.BOOLEAN -> Form.Field.Type.BOOLEAN

        FormResponse.FieldResponse.TypeResponse.PHONE_NUMBER -> Form.Field.Type.PHONE_NUMBER

        FormResponse.FieldResponse.TypeResponse.IMAGE -> Form.Field.Type.IMAGE
        FormResponse.FieldResponse.TypeResponse.AUDIO -> Form.Field.Type.AUDIO
        FormResponse.FieldResponse.TypeResponse.VIDEO -> Form.Field.Type.VIDEO
        FormResponse.FieldResponse.TypeResponse.DOCUMENT -> Form.Field.Type.DOCUMENT
        FormResponse.FieldResponse.TypeResponse.FILE -> Form.Field.Type.FILE
    }