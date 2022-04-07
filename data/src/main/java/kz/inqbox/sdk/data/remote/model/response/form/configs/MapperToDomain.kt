package kz.inqbox.sdk.data.remote.model.response.form.configs

import kz.inqbox.sdk.data.remote.model.response.form.FormResponse
import kz.inqbox.sdk.domain.model.form.Form

fun FormResponse.ConfigsResponse.toFormConfigs(): Form.Configs =
    Form.Configs(
        assignees = assignee,
        projectId = projectId
    )