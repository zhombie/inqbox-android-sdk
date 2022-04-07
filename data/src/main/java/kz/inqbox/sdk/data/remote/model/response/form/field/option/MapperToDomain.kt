package kz.inqbox.sdk.data.remote.model.response.form.field.option

import kz.inqbox.sdk.data.remote.model.response.form.FormResponse
import kz.inqbox.sdk.domain.model.form.Form

fun FormResponse.FieldResponse.OptionResponse.toOption(): Form.Field.Option =
    Form.Field.Option(
        id = id,
        title = title,
        parentId = parentId,
        key = key,
        value = value
    )
