package kz.inqbox.sdk.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// TODO: Divide into classes: System & Client; Make more detailed properties
@Deprecated("It doesn't provide required definition")
@Parcelize
data class User constructor(
    val userId: Long,
    val token: String
) : Parcelable