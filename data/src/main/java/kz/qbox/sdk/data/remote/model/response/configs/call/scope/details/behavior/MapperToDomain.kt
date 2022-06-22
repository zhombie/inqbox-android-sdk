package kz.qbox.sdk.data.remote.model.response.configs.call.scope.details.behavior

import kz.qbox.sdk.data.remote.model.response.configs.ConfigsResponse
import kz.qbox.sdk.domain.model.configs.Configs

fun ConfigsResponse.CallScopeResponse.DetailsResponse.BehaviorResponse?.toNestableBehavior(
): Configs.Nestable.Extra.Behavior =
    when (this) {
        ConfigsResponse.CallScopeResponse.DetailsResponse.BehaviorResponse.UNKNOWN ->
            Configs.Nestable.Extra.Behavior.UNKNOWN
        ConfigsResponse.CallScopeResponse.DetailsResponse.BehaviorResponse.REGULAR ->
            Configs.Nestable.Extra.Behavior.REGULAR
        ConfigsResponse.CallScopeResponse.DetailsResponse.BehaviorResponse.REQUEST_LOCATION ->
            Configs.Nestable.Extra.Behavior.REQUEST_LOCATION
        else ->
            Configs.Nestable.Extra.Behavior.UNKNOWN
    }
