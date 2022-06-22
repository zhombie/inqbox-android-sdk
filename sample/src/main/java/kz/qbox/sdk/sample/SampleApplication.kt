package kz.qbox.sdk.sample

import kz.garage.locale.LocaleManager
import kz.garage.locale.base.LocaleManagerBaseApplication
import kz.qbox.sdk.domain.model.language.Language
import kz.qbox.sdk.socket.Socket
import kz.qbox.sdk.webrtc.WebRTC
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