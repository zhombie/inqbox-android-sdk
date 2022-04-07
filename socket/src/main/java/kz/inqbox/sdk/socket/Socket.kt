package kz.inqbox.sdk.socket

import kz.inqbox.sdk.domain.model.language.Language

object Socket {

    private var isLoggingEnabled: Boolean = false
    private var language: Language = Language.DEFAULT

    fun init(
        isLoggingEnabled: Boolean = false,
        language: Language = Language.DEFAULT
    ): Boolean {
        return setLoggingEnabled(isLoggingEnabled) && setLanguage(language)
    }

    fun isLoggingEnabled(): Boolean = isLoggingEnabled

    fun getLanguage(): Language = language

    fun setLoggingEnabled(isLoggingEnabled: Boolean): Boolean {
        this.isLoggingEnabled = isLoggingEnabled
        return this.isLoggingEnabled == isLoggingEnabled
    }

    fun setLanguage(language: Language): Boolean {
        this.language = language
        return this.language == language
    }

}