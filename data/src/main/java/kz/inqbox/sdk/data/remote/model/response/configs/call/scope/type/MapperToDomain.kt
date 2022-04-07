package kz.inqbox.sdk.data.remote.model.response.configs.call.scope.type

import kz.inqbox.sdk.data.remote.model.response.configs.ConfigsResponse
import kz.inqbox.sdk.domain.model.configs.Configs

fun ConfigsResponse.CallScopeResponse.TypeResponse.toNestableType(): Configs.Nestable.Type =
    when (this) {
        ConfigsResponse.CallScopeResponse.TypeResponse.FOLDER -> Configs.Nestable.Type.FOLDER
        ConfigsResponse.CallScopeResponse.TypeResponse.LINK -> Configs.Nestable.Type.LINK
    }
