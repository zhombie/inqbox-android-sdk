package kz.inqbox.sdk.domain.model.upload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadResult constructor(
    val hash: String,
    val title: String,
    val url: String
) : Parcelable