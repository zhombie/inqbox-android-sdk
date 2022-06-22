package kz.qbox.sdk.data.remote.model.response.i18n

import kz.qbox.sdk.domain.model.i18n.I18NString

fun I18NStringResponse.toI18NString(): I18NString {
    val kk = if (!kk.isNullOrBlank()) kk else kz
    return I18NString(kk = kk, ru = ru, en = en)
}