package kz.inqbox.sdk.domain.model.response.group.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.inqbox.sdk.domain.model.response.base.BaseResponse
import kz.inqbox.sdk.domain.model.language.Language

@Parcelize
open class BaseGroupResponse constructor(
    open val id: Long,
    open val title: String,
    open val language: Language
) : BaseResponse(), Parcelable