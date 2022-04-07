package kz.inqbox.sdk.data.remote.model.response.form.field.configs

import kz.inqbox.sdk.data.remote.model.response.form.FormResponse
import kz.inqbox.sdk.domain.model.form.Form

fun FormResponse.FieldResponse.ConfigsResponse.toFormFieldConfigs(): Form.Field.Configs =
    Form.Field.Configs(
        isMultipleSelection = isMultipleSelection,
        maxSelectionCount = maxSelectionCount,
        key = key,
        regexp = regexp,
        regexpExplanation = regexpExplanation,
        inputTextMaxLength = inputTextMaxLength,
        inputTextMaxLines = inputTextMaxLines
    )
