package kz.qbox.sdk.data.remote.model.response.form.configs

import kz.qbox.sdk.data.remote.model.response.form.FormResponse
import kz.qbox.sdk.domain.model.form.Form

fun FormResponse.ConfigsResponse.toFormConfigs(): Form.Configs =
    Form.Configs(
        assignees = assignee,
        projectId = projectId
    )