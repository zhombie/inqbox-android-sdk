package kz.qbox.sdk.data.remote.model.response.configs.booleans

import kz.qbox.sdk.data.remote.model.response.configs.ConfigsResponse
import kz.qbox.sdk.domain.model.configs.Configs

fun ConfigsResponse.BooleansResponse.toPreferences(): Configs.Preferences {
    return Configs.Preferences(
        isChatBotEnabled = isChatBotEnabled == true,
        isPhonesListShown = isPhonesListShown == true,
        isContactSectionsShown = isContactSectionsShown == true,
        isAudioCallEnabled = isAudioCallEnabled == true,
        isVideoCallEnabled = isVideoCallEnabled == true,
        isServicesEnabled = isServicesEnabled == true,
        isCallAgentsScoped = isOperatorsScoped == true
    )
}