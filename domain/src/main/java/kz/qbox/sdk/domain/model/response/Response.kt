package kz.qbox.sdk.domain.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.garage.multimedia.store.model.Content
import kz.qbox.sdk.domain.model.response.base.BaseResponse

@Parcelize
data class Response constructor(
    val id: Long,
    val messageId: String? = null,
    val text: String? = null,
    val time: Long = -1L,
    val attachments: List<Content> = emptyList(),
    val form: Form? = null
) : BaseResponse(), Parcelable {

    @Parcelize
    data class Form constructor(
        val id: Long,
        val title: String,
        val prompt: String? = null
    ) : Parcelable

}