package kz.inqbox.sdk.sample.samples.socket

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kz.garage.activity.view.bind
import kz.garage.kotlin.simpleNameOf
import kz.inqbox.sdk.domain.model.language.Language
import kz.inqbox.sdk.sample.BuildConfig
import kz.inqbox.sdk.sample.R
import kz.inqbox.sdk.socket.Socket
import kz.inqbox.sdk.socket.SocketClient
import kz.inqbox.sdk.socket.listener.SocketStateListener
import kz.inqbox.sdk.socket.repository.SocketRepository
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.Executors

@SuppressLint("SetTextI18n")
class SocketActivity : AppCompatActivity(), SocketStateListener {

    companion object {
        private val TAG = simpleNameOf<SocketActivity>()
    }

    private val statusView by bind<MaterialTextView>(R.id.statusView)
    private val button by bind<MaterialButton>(R.id.button)

    private var socketRepository: SocketRepository? = null

    private val handler by lazy(LazyThreadSafetyMode.NONE) {
        HandlerCompat.createAsync(Looper.getMainLooper())
    }

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)

        statusView.text = "Status: IDLE"
        button.text = "Connect"

        button.setOnClickListener {
            if (socketRepository?.isConnected() == true) {
                statusView.text = "Status: Disconnecting"

                socketRepository?.disconnect()
            } else {
                statusView.text = "Status: Connecting"

                if (socketRepository == null) {
                    socketRepository = SocketClient.getInstance()

                    socketRepository?.create(
                        BuildConfig.SOCKET_BASE_URL,
                        okHttpClient = OkHttpClient.Builder()
                            .apply {
                                var handshakeCertificates: HandshakeCertificates? = null
                                try {
                                    val certificateFactory = CertificateFactory.getInstance("X.509")

                                    val certificate =
                                        certificateFactory.generateCertificateSafely(R.raw.certificate)

                                    handshakeCertificates = HandshakeCertificates.Builder()
                                        .addTrustedCertificate(certificate)
                                        .addPlatformTrustedCertificates()
                                        .build()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                if (handshakeCertificates != null) {
                                    sslSocketFactory(
                                        handshakeCertificates.sslSocketFactory(),
                                        handshakeCertificates.trustManager
                                    )
                                }
                            }
                            .build()
                    )

                    socketRepository?.setSocketStateListener(this)

                    socketRepository?.registerSocketConnectEventListener()
                    socketRepository?.registerSocketDisconnectEventListener()

                    if (socketRepository?.isConnected() == false) {
                        socketRepository?.connect()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        socketRepository?.removeAllListeners()
        socketRepository?.release()
        socketRepository?.disconnect()
        socketRepository = null
    }

    /**
     * [SocketStateListener] implementation
     */

    override fun onSocketConnect() {
        Log.d(TAG, "onSocketConnect()")

        handler.post {
            statusView.text = listOf(
                "Status: Connected",
                "id: ${socketRepository?.getId()}"
            ).joinToString("\n")

            button.text = "Disconnect"
        }
    }

    override fun onSocketDisconnect() {
        Log.d(TAG, "onSocketDisconnect()")

        executor.execute {
            socketRepository?.release()
            socketRepository = null
        }

        handler.post {
            statusView.text = "Status: Disconnected"

            button.text = "Connect"
        }
    }

    private fun CertificateFactory.generateCertificateSafely(
        @RawRes certificateId: Int
    ): Certificate? {
        return try {
            resources.openRawResource(certificateId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }?.use {
            generateCertificate(it)
        }
    }

    private fun HandshakeCertificates.Builder.addTrustedCertificate(
        certificate: Certificate?
    ): HandshakeCertificates.Builder {
        if (certificate is X509Certificate) {
            addTrustedCertificate(certificate)
        }
        return this
    }

}