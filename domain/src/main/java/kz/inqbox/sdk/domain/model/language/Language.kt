package kz.inqbox.sdk.domain.model.language

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Language internal constructor(
    val key: String,
    val representation: String,
    val id: Long
) : Parcelable {

    companion object {
        val DEFAULT: Language
            get() = by(Locale.getDefault().language)

        val KAZAKH: Language
            get() = Language(Key.KK, Representation.KK, ID.KK)

        val RUSSIAN: Language
            get() = Language(Key.RU, Representation.RU, ID.RU)

        val ENGLISH: Language
            get() = Language(Key.EN, Representation.EN, ID.EN)

        fun getSupportedLanguages(): Array<Language> =
            arrayOf(KAZAKH, RUSSIAN)

        fun from(locale: Locale): Language =
            by(locale.language)

        fun by(key: String): Language =
            when (key) {
                Key.KK.value -> KAZAKH
                Key.RU.value -> RUSSIAN
                else -> RUSSIAN
            }
    }

    @Parcelize
    enum class Key constructor(val value: String) : Parcelable {
        KK("kk"),
        RU("ru"),
        EN("en")
    }

    @Parcelize
    enum class Representation constructor(val value: String) : Parcelable {
        KK("Қаз"),
        RU("Рус"),
        EN("Eng")
    }

    @Parcelize
    enum class ID constructor(val value: Long) : Parcelable {
        KK(2),
        RU(1),
        EN(3);

        companion object {
            fun by(value: Long): ID {
                return when (value) {
                    KK.value -> KK
                    RU.value -> RU
                    EN.value -> EN
                    else -> RU
                }
            }
        }
    }

    constructor(
        key: Key,
        representation:
        Representation, id: ID
    ) : this(key = key.value, representation = representation.value, id = id.value)

    val locale: Locale
        get() = Locale(key)

    override fun toString(): String = key

}