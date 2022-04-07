package kz.inqbox.sdk.data.remote.model.response.form.field.autofill

import kz.inqbox.sdk.data.remote.model.response.form.FormResponse
import kz.inqbox.sdk.domain.model.form.Form

fun FormResponse.FieldResponse.AutofillResponse.toFormFieldAutofill(): Form.Field.Autofill =
    Form.Field.Autofill(
        qualifier = when (qualifier) {
            "user.first_name" -> Form.Field.Autofill.Qualifier.USER_FIRST_NAME
            "user.last_name" -> Form.Field.Autofill.Qualifier.USER_LAST_NAME
            "user.patronymic" -> Form.Field.Autofill.Qualifier.USER_PATRONYMIC
            "user.full_name" -> Form.Field.Autofill.Qualifier.USER_FULL_NAME
            "user.iin" -> Form.Field.Autofill.Qualifier.USER_IIN
            "user.email" -> Form.Field.Autofill.Qualifier.USER_EMAIL
            "user.phone_number" -> Form.Field.Autofill.Qualifier.USER_PHONE_NUMBER
            "user.geolocation" -> Form.Field.Autofill.Qualifier.USER_GEOLOCATION
            else -> Form.Field.Autofill.Qualifier.UNKNOWN
        }
    )