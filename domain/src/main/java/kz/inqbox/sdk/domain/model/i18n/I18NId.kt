package kz.inqbox.sdk.domain.model.i18n

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.inqbox.sdk.domain.model.language.Language
import java.util.*

@Parcelize
data class I18NId constructor(
    val kk: Long?,
    val ru: Long?,
    val en: Long?
) : Parcelable {

    fun get(language: Language? = null): Long? =
        when (language?.key ?: Language.from(Locale.getDefault()).key) {
            Language.KAZAKH.key -> kk
            Language.RUSSIAN.key -> ru
            Language.ENGLISH.key -> en
            else -> ru
        }

}