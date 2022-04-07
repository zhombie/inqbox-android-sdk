package kz.inqbox.sdk.sample

import kz.garage.locale.LocaleManager
import kz.garage.locale.base.LocaleManagerBaseApplication
import kz.inqbox.sdk.domain.model.language.Language
import kz.inqbox.sdk.socket.Socket
import kz.inqbox.sdk.webrtc.WebRTC
import java.util.*

class SampleApplication : LocaleManagerBaseApplication() {

    override fun onCreate() {
        super.onCreate()

        Socket.init(isLoggingEnabled = true, language = Language.RUSSIAN)

        WebRTC.init(isLoggingEnabled = true)
    }

    override fun initializeLocaleManager() {
        LocaleManager.initialize(
            context = this,
            supportedLocales = listOf(
                Locale.ENGLISH,
                Locale("ru"),
                Locale("kk")
            )
        )
    }

}