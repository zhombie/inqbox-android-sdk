package kz.inqbox.sdk.sample.samples.webrtc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kz.garage.activity.intent.startActivity
import kz.garage.activity.view.bind
import kz.inqbox.sdk.sample.R
import kz.inqbox.sdk.webrtc.WebRTC

class WebRTCActivity : AppCompatActivity() {

    private val button by bind<MaterialButton>(R.id.button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        button.setOnClickListener {
            startActivity<PreviewActivity>()
        }
    }

}