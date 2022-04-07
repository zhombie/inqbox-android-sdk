package kz.inqbox.sdk.socket

import kz.inqbox.sdk.domain.model.language.Language

object Socket {

    private var isLoggingEnabled: Boolean = false
    private var language: Language = Language.DEFAULT

    fun init(isLoggingEnabled: Boolean, language: Language) {
        Socket.isLoggingEnabled = isLoggingEnabled
        Socket.language = language
    }

    fun isLoggingEnabled(): Boolean = isLoggingEnabled

    fun getLanguage(): Language = language

}