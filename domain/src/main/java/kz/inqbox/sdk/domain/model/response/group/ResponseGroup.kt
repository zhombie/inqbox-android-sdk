package kz.inqbox.sdk.domain.model.response.group

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.inqbox.sdk.domain.model.response.Response
import kz.inqbox.sdk.domain.model.response.group.base.BaseGroupResponse
import kz.inqbox.sdk.domain.model.language.Language

@Parcelize
data class ResponseGroup constructor(
    override val id: Long,
    override val title: String,
    override val language: Language,
    val isPrimary: Boolean = false,
    val children: List<BaseGroupResponse>
) : BaseGroupResponse(id = id, title = title, language = language), Parcelable {

    @Parcelize
    data class Child constructor(
        override val id: Long,
        override val title: String,
        override val language: Language,
        val responses: List<Response>
    ) : BaseGroupResponse(id = id, title = title, language = language), Parcelable

}
