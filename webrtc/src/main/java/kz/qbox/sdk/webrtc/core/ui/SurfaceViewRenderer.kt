package kz.qbox.sdk.webrtc.core.ui

import android.content.Context
import android.util.AttributeSet

open class SurfaceViewRenderer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : org.webrtc.SurfaceViewRenderer(context, attrs)