package kz.qbox.sdk.sample.samples.webrtc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kz.garage.activity.view.bind
import kz.garage.kotlin.simpleNameOf
import kz.qbox.sdk.domain.model.webrtc.IceServer
import kz.qbox.sdk.sample.R
import kz.qbox.sdk.webrtc.Options
import kz.qbox.sdk.webrtc.PeerConnectionClient
import kz.qbox.sdk.webrtc.core.ui.SurfaceViewRenderer

class PreviewActivity : AppCompatActivity() {

    companion object {
        private val TAG = simpleNameOf<PreviewActivity>()

        private val ICE_SERVERS by lazy {
            listOf(
                IceServer(
                    url = "stun:global.stun.twilio.com:3478?transport=udp",
                    urls = "stun:global.stun.twilio.com:3478?transport=udp"
                )
            )
        }

        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private val fullSurfaceViewRenderer by bind<SurfaceViewRenderer>(R.id.fullSurfaceViewRenderer)
    private val miniSurfaceViewRenderer by bind<SurfaceViewRenderer>(R.id.miniSurfaceViewRenderer)
    private val startOrStopButton by bind<MaterialButton>(R.id.startOrStopButton)
    private val mirrorButton by bind<MaterialButton>(R.id.mirrorButton)
    private val switchButton by bind<MaterialButton>(R.id.switchButton)
    private val shutdownButton by bind<MaterialButton>(R.id.shutdownButton)

    private var peerConnectionClient: PeerConnectionClient? = null

    private val applicationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "ActivityResultContracts.StartActivityForResult() -> Success")
            } else {
                Log.d(TAG, "ActivityResultContracts.StartActivityForResult() -> Fail")
            }
        }

    private val requestPermissions = requestMultiplePermissions(
        onAllGranted = {
            if (peerConnectionClient == null) {
                peerConnectionClient = PeerConnectionClient(applicationContext)
            }
            peerConnectionClient?.setLocalSurfaceView(fullSurfaceViewRenderer)
            peerConnectionClient?.createPeerConnection(
                Options(
                    isLocalAudioEnabled = true,
                    isLocalVideoEnabled = true,
                    videoCodecHwAcceleration = true,
                    iceServers = ICE_SERVERS
                )
            )
            peerConnectionClient?.initLocalCameraStream(
                isMirrored = true,
                isZOrderMediaOverlay = false
            )
            peerConnectionClient?.addLocalStreamToPeer()
        },
        onDenied = {
            launchApplicationSettings()
        },
        onExplained = {
            AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Grant access to permissions: ${PERMISSIONS.contentToString()}")
                .setPositiveButton("Grant access") { dialog, _ ->
                    dialog.dismiss()
                    launchApplicationSettings()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        var isStarted = true
        startOrStopButton.setOnClickListener {
            isStarted = if (isStarted) {
                peerConnectionClient?.stopLocalVideoCapture()
                false
            } else {
                peerConnectionClient?.startLocalVideoCapture()
                true
            }
        }

        var isMirrored = true
        mirrorButton.setOnClickListener {
            isMirrored = if (isMirrored) {
                peerConnectionClient?.setLocalVideoStreamMirror(false)
                false
            } else {
                peerConnectionClient?.setLocalVideoStreamMirror(true)
                true
            }
        }

        var isStreamSet = false
        switchButton.setOnClickListener {
            isStreamSet = if (isStreamSet) {
                peerConnectionClient?.setLocalVideoSink(
                    surfaceViewRenderer = fullSurfaceViewRenderer,
                    isMirrored = true,
                    isZOrderMediaOverlay = false
                )
                false
            } else {
                peerConnectionClient?.setLocalVideoSink(
                    surfaceViewRenderer = miniSurfaceViewRenderer,
                    isMirrored = true,
                    isZOrderMediaOverlay = false
                )
                true
            }
        }

        shutdownButton.setOnClickListener {
            dispose()
        }

        requestPermissions()
    }

    override fun onResume() {
        super.onResume()

        peerConnectionClient?.startLocalVideoCapture()
    }

    override fun onPause() {
        super.onPause()

        peerConnectionClient?.stopLocalVideoCapture()
    }

    override fun onDestroy() {
        dispose()

        super.onDestroy()
    }

    private fun requestPermissions() {
        requestPermissions.launch(PERMISSIONS)
    }

    private fun launchApplicationSettings() {
        applicationSettingsLauncher.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
        )
    }

    private fun dispose() {
        try {
            fullSurfaceViewRenderer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            miniSurfaceViewRenderer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        peerConnectionClient?.dispose()
        peerConnectionClient = null
    }

}