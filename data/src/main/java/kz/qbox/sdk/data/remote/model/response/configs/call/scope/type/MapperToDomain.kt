package kz.qbox.sdk.data.remote.model.response.configs.call.scope.type

import kz.qbox.sdk.data.remote.model.response.configs.ConfigsResponse
import kz.qbox.sdk.domain.model.configs.Configs

fun ConfigsResponse.CallScopeResponse.TypeResponse.toNestableType(): Configs.Nestable.Type =
    when (this) {
        ConfigsResponse.CallScopeResponse.TypeResponse.FOLDER -> Configs.Nestable.Type.FOLDER
        ConfigsResponse.CallScopeResponse.TypeResponse.LINK -> Configs.Nestable.Type.LINK
    }
