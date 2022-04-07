package kz.inqbox.sdk.domain.model.webrtc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AdapterType constructor(val bitMask: Int) : Parcelable {
    UNKNOWN(0),
    ETHERNET(1),
    WIFI(2),
    CELLULAR(4),
    VPN(8),
    LOOPBACK(16),
    ADAPTER_TYPE_ANY(32);
}