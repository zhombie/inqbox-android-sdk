package kz.inqbox.sdk.webrtc

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import kz.inqbox.sdk.domain.model.webrtc.IceConnectionState
import kz.inqbox.sdk.webrtc.audio.RTCAudioManager
import kz.inqbox.sdk.webrtc.core.constraints.*
import kz.inqbox.sdk.webrtc.core.model.Target
import kz.inqbox.sdk.webrtc.core.processor.ProxyVideoSink
import kz.inqbox.sdk.webrtc.core.ui.SurfaceViewRenderer
import kz.inqbox.sdk.webrtc.logger.Logger
import kz.inqbox.sdk.webrtc.mapper.*
import kz.inqbox.sdk.webrtc.utils.*
import org.webrtc.*
import org.webrtc.RendererCommon.ScalingType
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class PeerConnectionClient private constructor(
    private val contextReference: WeakReference<Context>,

    var options: Options = Options(),

    var localSurfaceViewRenderer: SurfaceViewRenderer? = null,
    var remoteSurfaceViewRenderer: SurfaceViewRenderer? = null
) {

    companion object {
        private val TAG = PeerConnectionClient::class.java.simpleName
    }

    constructor(
        context: Context,
        options: Options = Options(),
        localSurfaceViewRenderer: SurfaceViewRenderer? = null,
        remoteSurfaceViewRenderer: SurfaceViewRenderer? = null
    ) : this(
        contextReference = WeakReference(context),
        options = options,
        localSurfaceViewRenderer = localSurfaceViewRenderer,
        remoteSurfaceViewRenderer = remoteSurfaceViewRenderer
    )

    private val context: Context?
        get() = contextReference.get()

    private val uiThread: Handler by lazy(LazyThreadSafetyMode.NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.getMainLooper())
        } else {
            Handler(Looper.getMainLooper())
        }
    }

    private val executor = Executors.newSingleThreadExecutor()

    private var iceServers: List<PeerConnection.IceServer>? = null

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    private var eglBase: EglBase? = null

    private val sdpObserver = InnerSdpObserver()

    private var encoderFactory: VideoEncoderFactory? = null
    private var decoderFactory: VideoDecoderFactory? = null

    private var localAudioSource: AudioSource? = null
    private var localVideoSource: VideoSource? = null

    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private var localMediaStream: MediaStream? = null
    private var remoteMediaStream: MediaStream? = null

    private var localVideoCapturer: VideoCapturer? = null

    private var localAudioTrack: AudioTrack? = null
    private var remoteAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null
    private var remoteVideoTrack: VideoTrack? = null

    private var localVideoSink: ProxyVideoSink? = null
    private var remoteVideoSink: ProxyVideoSink? = null

    private var localVideoSinkTarget: Target? = null
    private var remoteVideoSinkTarget: Target? = null

    private var localSessionDescription: SessionDescription? = null

    private var localVideoSender: RtpSender? = null

    private var isInitiator = false

    private var localVideoScalingType: ScalingType? = null
    private var remoteVideoScalingType: ScalingType? = null

    private var listener: Listener? = null
    private var cameraBehaviorListener: CameraBehaviorListener? = null

    private var audioDeviceModule: AudioDeviceModule? = null

    private var audioManager: RTCAudioManager? = null

    private val cameraEventsHandler by lazy { CameraEventsHandler() }

    private val audioBooleanConstraints by lazy {
        RTCConstraints<AudioBooleanConstraints, Boolean>().apply {
//            addMandatoryConstraint(AudioBooleanConstraints.DISABLE_AUDIO_PROCESSING, true)
        }
    }

    private val audioIntegerConstraints by lazy {
        RTCConstraints<AudioIntegerConstraints, Int>()
    }

    private val offerAnswerConstraints by lazy {
        RTCConstraints<OfferAnswerConstraints, Boolean>().apply {
            addMandatoryConstraint(OfferAnswerConstraints.OFFER_TO_RECEIVE_AUDIO, true)
        }
    }

    private val peerConnectionConstraints by lazy {
        RTCConstraints<PeerConnectionConstraints, Boolean>().apply {
//            addMandatoryConstraint(PeerConnectionConstraints.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, true)
//            addMandatoryConstraint(PeerConnectionConstraints.GOOG_CPU_OVERUSE_DETECTION, true)
        }
    }

    @Throws(IllegalStateException::class)
    fun createPeerConnection(
        options: Options,
        listener: Listener? = null,
        cameraBehaviorListener: CameraBehaviorListener? = null
    ): PeerConnection? {
        Logger.debug(TAG, "createPeerConnection() -> options: $options")

        this.options = options

        eglBase = EglBase.create()

        if (options.iceServers.any { it.url.isBlank() || it.urls.isBlank() }) {
            iceServers = emptyList()
        } else {
            iceServers = options.iceServers.map {
                val builder = when {
                    it.url.isNotBlank() ->
                        PeerConnection.IceServer.builder(it.url)
                    it.urls.isNotBlank() ->
                        PeerConnection.IceServer.builder(it.urls)
                    else ->
                        throw IllegalStateException("url || urls is null or blank. Please provide anything.")
                }
                builder.setUsername(it.username ?: "")
                builder.setPassword(it.credential ?: "")
                builder.createIceServer()
            }
        }

        Logger.debug(TAG, "iceServers: $iceServers")

        this.listener = listener
        this.cameraBehaviorListener = cameraBehaviorListener

        isInitiator = false
        localSessionDescription = null

        options.audioBooleanConstraints?.let {
            audioBooleanConstraints += it
        }
        options.audioIntegerConstraints?.let {
            audioIntegerConstraints += it
        }
        options.peerConnectionConstraints?.let {
            peerConnectionConstraints += it
        }
        options.offerAnswerConstraints?.let {
            if (options.isLocalVideoEnabled || options.isRemoteVideoEnabled) {
                offerAnswerConstraints += RTCConstraints<OfferAnswerConstraints, Boolean>().apply {
                    addMandatoryConstraint(OfferAnswerConstraints.OFFER_TO_RECEIVE_VIDEO, true)
                }
            }
            offerAnswerConstraints += it
        }

        val future = executor.submit(Callable {
            if (context == null) return@Callable null

            val initializationOptions = PeerConnectionFactory.InitializationOptions
                .builder(context?.applicationContext)
                .setEnableInternalTracer(true)
                .createInitializationOptions()

            PeerConnectionFactory.initialize(initializationOptions)

            val peerConnectionFactoryOptions = PeerConnectionFactory.Options()
            peerConnectionFactoryOptions.disableNetworkMonitor = true

            if (options.videoCodecHwAcceleration) {
                encoderFactory = DefaultVideoEncoderFactory(
                    eglBase?.eglBaseContext,
                    true,  /* enableIntelVp8Encoder */
                    true  /* enableH264HighProfile */
                )

                decoderFactory = DefaultVideoDecoderFactory(eglBase?.eglBaseContext)
            } else {
                encoderFactory = SoftwareVideoEncoderFactory()
                decoderFactory = SoftwareVideoDecoderFactory()
            }

            audioDeviceModule = createJavaAudioDeviceModule()

            peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(peerConnectionFactoryOptions)
                .setAudioDeviceModule(audioDeviceModule)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory()

            peerConnection = peerConnectionFactory?.let { createPeerConnectionInternally(it) }

            return@Callable peerConnection
        })

        return future.get()
    }

    private fun createJavaAudioDeviceModule(): JavaAudioDeviceModule {
        return JavaAudioDeviceModule.builder(context)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .setAudioTrackErrorCallback(object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                override fun onWebRtcAudioTrackInitError(p0: String?) {
                    Logger.error(TAG, "$p0")
                }

                override fun onWebRtcAudioTrackError(p0: String?) {
                    Logger.error(TAG, "$p0")
                }

                override fun onWebRtcAudioTrackStartError(
                    p0: JavaAudioDeviceModule.AudioTrackStartErrorCode?,
                    p1: String?
                ) {
                    Logger.error(TAG, "$p0, $p1")
                }
            })
            .createAudioDeviceModule()
    }

    fun setLocalSurfaceView(surfaceView: SurfaceViewRenderer?): Boolean {
        Logger.debug(TAG, "setLocalSurfaceView() -> $surfaceView")

        localSurfaceViewRenderer = surfaceView
        return localSurfaceViewRenderer == surfaceView
    }

    fun initLocalCameraStream(
        isMirrored: Boolean = false,
        isZOrderMediaOverlay: Boolean = true
    ): Boolean {
        Logger.debug(TAG, "initLocalCameraStream() -> isMirrored: $isMirrored")

        if (localSurfaceViewRenderer == null) {
            Logger.error(TAG, "Local SurfaceViewRenderer is null.")
            return false
        }

        runOnUiThread {
            localSurfaceViewRenderer?.release()

            localSurfaceViewRenderer?.init(eglBase?.eglBaseContext, null)
            localSurfaceViewRenderer?.setEnableHardwareScaler(true)
            localSurfaceViewRenderer?.setMirror(isMirrored)
            localSurfaceViewRenderer?.setZOrderMediaOverlay(isZOrderMediaOverlay)

            localVideoScalingType = ScalingType.SCALE_ASPECT_FILL
            localSurfaceViewRenderer?.setScalingType(localVideoScalingType)
        }

        return true
    }

    fun setRemoteSurfaceView(surfaceView: SurfaceViewRenderer?): Boolean {
        Logger.debug(TAG, "setRemoteSurfaceView() -> $surfaceView")

        remoteSurfaceViewRenderer = surfaceView
        return remoteSurfaceViewRenderer == surfaceView
    }

    fun initRemoteCameraStream(
        isMirrored: Boolean = false,
        isZOrderMediaOverlay: Boolean = false
    ): Boolean {
        Logger.debug(TAG, "initRemoteCameraStream() -> isMirrored: $isMirrored")

        if (remoteSurfaceViewRenderer == null) {
            Logger.error(TAG, "Remote SurfaceViewRenderer is null.")
            return false
        }

        runOnUiThread {
            remoteSurfaceViewRenderer?.release()

            remoteSurfaceViewRenderer?.init(eglBase?.eglBaseContext, null)
            remoteSurfaceViewRenderer?.setEnableHardwareScaler(true)
            remoteSurfaceViewRenderer?.setMirror(isMirrored)
            remoteSurfaceViewRenderer?.setZOrderMediaOverlay(isZOrderMediaOverlay)

            remoteVideoScalingType = ScalingType.SCALE_ASPECT_FILL
            remoteSurfaceViewRenderer?.setScalingType(remoteVideoScalingType)
        }

        return true
    }

    fun setLocalVideoScalingType(scalingType: kz.inqbox.sdk.webrtc.core.model.ScalingType) {
        runOnUiThread {
            localVideoScalingType = ScalingTypeMapper.map(scalingType)
            localSurfaceViewRenderer?.setScalingType(localVideoScalingType)
        }
    }

    fun setRemoteVideoScalingType(scalingType: kz.inqbox.sdk.webrtc.core.model.ScalingType) {
        runOnUiThread {
            remoteVideoScalingType = ScalingTypeMapper.map(scalingType)
            remoteSurfaceViewRenderer?.setScalingType(remoteVideoScalingType)
        }
    }

    fun addLocalStreamToPeer(): Boolean {
        Logger.debug(TAG, "addLocalStreamToPeer()")

        localMediaStream = peerConnectionFactory?.createLocalMediaStream("ARDAMS")

        val audioTrack = createAudioTrack()
        if (audioTrack != null) {
            localMediaStream?.addTrack(audioTrack)
        }

        val videoTrack = createVideoTrack()
        if (videoTrack != null) {
            localMediaStream?.addTrack(videoTrack)
            findVideoSender()
        }

        var isStreamAdded = false
        if (localMediaStream != null) {
            isStreamAdded = peerConnection?.addStream(localMediaStream) == true
        }

        startAudioManager()

        return isStreamAdded
    }

    fun addRemoteStreamToPeer(mediaStream: MediaStream): Boolean {
        Logger.debug(TAG, "addRemoteStreamToPeer() -> mediaStream: $mediaStream")

        try {
            val id = mediaStream.id
            Logger.debug(TAG, "addRemoteStreamToPeer() [MediaStream exists] -> id: $id")
        } catch (e: IllegalStateException) {
            Logger.debug(TAG, "addRemoteStreamToPeer() [MediaStream does not exist]")
            return false
        }

        executor.execute {
            remoteMediaStream = mediaStream

            if (mediaStream.audioTracks.isNotEmpty()) {
                remoteAudioTrack = mediaStream.audioTracks.first()
                remoteAudioTrack?.setEnabled(options.isRemoteAudioEnabled)
            }

            if (mediaStream.videoTracks.isNotEmpty()) {
                remoteVideoTrack = mediaStream.videoTracks.first()
                remoteVideoTrack?.setEnabled(options.isRemoteVideoEnabled)

                if (remoteSurfaceViewRenderer == null) {
                    Logger.error(TAG, "Remote SurfaceViewRenderer is null.")
                } else {
                    remoteVideoSink = ProxyVideoSink("RemoteVideoSink")
                    remoteVideoSink?.setTarget(remoteSurfaceViewRenderer)
                    remoteVideoTrack?.addSink(remoteVideoSink)
                }
            }
        }

        return true
    }

    private fun startAudioManager() {
        Logger.debug(TAG, "startAudioManager()")

        runOnUiThread {
            audioManager = RTCAudioManager.create(context)
            audioManager?.start { selectedAudioDevice, availableAudioDevices ->
                Logger.debug(TAG, "audioManager: $availableAudioDevices, $selectedAudioDevice")
            }
        }
    }

    private fun createVideoTrack(): VideoTrack? {
        Logger.debug(TAG, "createVideoTrack()")

        if (localSurfaceViewRenderer == null) {
            Logger.error(TAG, "Local SurfaceViewRenderer is null.")
            return null
        }

        return executor.submit(Callable {
            surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", eglBase?.eglBaseContext)

            localVideoSource = peerConnectionFactory?.createVideoSource(false)

            if (localVideoSource == null) {
                Logger.error(TAG, "Local VideoSource is null.")
                return@Callable null
            }

            localVideoCapturer = try {
                createVideoCapturer()
            } catch (e: Exception) {
                listener?.onLocalVideoCapturerCreateError(e)
                return@Callable null
            }

            localVideoCapturer?.initialize(
                surfaceTextureHelper,
                context,
                localVideoSource?.capturerObserver
            )

            localVideoCapturer?.startCapture(
                options.localVideoWidth,
                options.localVideoHeight,
                options.localVideoFPS
            )

            localVideoTrack = peerConnectionFactory?.createVideoTrack(
                options.localVideoTrackId,
                localVideoSource
            )
            localVideoTrack?.setEnabled(options.isLocalVideoEnabled)

            localVideoSink = ProxyVideoSink("LocalVideoSink")
            localVideoSink?.setTarget(localSurfaceViewRenderer)
            localVideoTrack?.addSink(localVideoSink)

            return@Callable localVideoTrack
        }).get()
    }

    private fun createAudioTrack(): AudioTrack? {
        Logger.debug(TAG, "createAudioTrack()")

        Logger.debug(TAG, "Audio constraints: ${getAudioMediaConstraints()}")

        return executor.submit(Callable{
            localAudioSource = peerConnectionFactory?.createAudioSource(getAudioMediaConstraints())

            localAudioTrack = peerConnectionFactory?.createAudioTrack(
                options.localAudioTrackId,
                localAudioSource
            )
            localAudioTrack?.setEnabled(options.isLocalAudioEnabled)

            return@Callable localAudioTrack
        }).get()
    }

    private fun createVideoCapturer(): VideoCapturer? {
        Logger.debug(TAG, "createVideoCapturer()")

        return if (useCamera2()) {
            createCameraCapturer(Camera2Enumerator(context))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        Logger.debug(TAG, "createCameraCapturer() -> enumerator: $enumerator")

        val deviceNames = enumerator.deviceNames
        // find the front facing camera and return it.
        deviceNames
            .filter { enumerator.isFrontFacing(it) }
            .mapNotNull { enumerator.createCapturer(it, cameraEventsHandler) }
            .forEach { return it }
        return null
    }

    private fun useCamera2(): Boolean = Camera2Enumerator.isSupported(context)

    private fun findVideoSender() {
        Logger.debug(TAG, "findVideoSender()")

        peerConnection?.let {
            for (sender in it.senders) {
                if (sender.track()?.kind() == MediaStreamTrack.VIDEO_TRACK_KIND) {
                    Logger.debug(TAG, "Found video sender.")
                    localVideoSender = sender
                }
            }
        }
    }

    fun setVideoMaxBitrate(maxBitrateKbps: Int?) {
        Logger.debug(TAG, "setVideoMaxBitrate() -> maxBitrateKbps: $maxBitrateKbps")

        executor.execute {
            if (peerConnection == null || localVideoSender == null) {
                return@execute
            }
            Logger.debug(TAG, "Requested max video bitrate: $maxBitrateKbps")
            if (localVideoSender == null) {
                Logger.debug(TAG, "Sender is not ready.")
                return@execute
            }
            val parameters = localVideoSender?.parameters
            if (parameters == null || parameters.encodings.size == 0) {
                Logger.debug(TAG, "RtpParameters are not ready.")
                return@execute
            }
            for (encoding in parameters.encodings) {
                // Null value means no limit.
                encoding.maxBitrateBps =
                    if (maxBitrateKbps == null) null else maxBitrateKbps * options.bpsInKbps
            }
            if (localVideoSender?.setParameters(parameters) == false) {
                Logger.debug(TAG, "RtpSender.setParameters failed.")
            }
            Logger.debug(TAG, "Configured max video bitrate to: $maxBitrateKbps")
        }
    }

    fun addRemoteIceCandidate(iceCandidate: kz.inqbox.sdk.domain.model.webrtc.IceCandidate) {
        Logger.debug(TAG, "addRemoteIceCandidate() -> iceCandidate: $iceCandidate")

        executor.execute {
            peerConnection?.addIceCandidate(IceCandidateMapper.map(iceCandidate))
        }
    }

    fun setRemoteDescription(sessionDescription: kz.inqbox.sdk.domain.model.webrtc.SessionDescription) {
        Logger.debug(TAG, "setRemoteDescription() -> sdp: $sessionDescription")

        executor.execute {
            var sdpDescription = CodecUtils.preferCodec(
                sessionDescription.description,
                CodecUtils.AUDIO_CODEC_OPUS,
                true
            )

            sdpDescription = CodecUtils.preferCodec(
                sdpDescription,
                CodecUtils.VIDEO_CODEC_VP9,
                false
            )

//            sdpDescription = Codec.setStartBitrate(Codec.AUDIO_CODEC_OPUS, false, sdpDescription, 32)

            peerConnection?.setRemoteDescription(
                sdpObserver,
                SessionDescription(
                    SessionDescriptionMapper.map(sessionDescription.type),
                    sdpDescription
                )
            )
        }
    }

    fun createOffer() {
        Logger.debug(TAG, "createOffer()")

        executor.execute {
            isInitiator = true
            peerConnection?.createOffer(sdpObserver, getOfferAnswerConstraints())
        }
    }

    fun createAnswer() {
        Logger.debug(TAG, "createAnswer()")

        executor.execute {
            isInitiator = false
            peerConnection?.createAnswer(sdpObserver, getOfferAnswerConstraints())
        }
    }

    private fun createPeerConnectionInternally(factory: PeerConnectionFactory): PeerConnection? {
        Logger.debug(TAG, "createPeerConnectionInternally() -> factory: $factory")

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers ?: emptyList())

        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.ALL

        val peerConnectionObserver = object : PeerConnection.Observer {
            override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {
                Logger.debug(TAG, "onSignalingChange() -> $signalingState")
            }

            override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
                Logger.debug(TAG, "onIceConnectionChange() -> $iceConnectionState")

                listener?.onIceConnectionChange(IceConnectionStateMapper.map(iceConnectionState))
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Logger.debug(TAG, "onIceConnectionReceivingChange() -> b: $b")
            }

            override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
                Logger.debug(TAG, "onIceGatheringChange() -> iceGatheringState: $iceGatheringState")
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Logger.debug(TAG, "onIceCandidate() -> iceCandidate: $iceCandidate")

                listener?.onLocalIceCandidate(
                    IceCandidateMapper.map(
                        iceCandidate,
                        AdapterTypeMapper.map(iceCandidate.adapterType)
                    )
                )
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Logger.debug(TAG, "onIceCandidatesRemoved() -> ${iceCandidates.contentToString()}")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Logger.debug(TAG, "onAddStream() -> mediaStream: $mediaStream")

                listener?.onAddRemoteStream(mediaStream)
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Logger.debug(TAG, "onRemoveStream() -> mediaStream: $mediaStream")

                listener?.onRemoveStream(mediaStream)
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Logger.debug(TAG, "onDataChannel() -> dataChannel: $dataChannel")
            }

            override fun onRenegotiationNeeded() {
                Logger.debug(TAG, "onRenegotiationNeeded()")

                listener?.onRenegotiationNeeded()
            }

            override fun onAddTrack(
                rtpReceiver: RtpReceiver?,
                mediaStreams: Array<out MediaStream>?
            ) {
                Logger.debug(TAG, "onAddTrack() -> $rtpReceiver, ${mediaStreams.contentToString()}")
            }

            override fun onRemoveTrack(rtpReceiver: RtpReceiver?) {
                Logger.debug(TAG, "onRemoveTrack() -> $rtpReceiver")
            }
        }
        return factory.createPeerConnection(rtcConfig, peerConnectionObserver)
    }

    fun onSwitchCamera(
        onDone: (isFrontFacing: Boolean) -> Unit,
        onError: (error: String?) -> Unit
    ) {
        executor.execute {
            val videoCapturer = localVideoCapturer
            if (videoCapturer is CameraVideoCapturer) {
                videoCapturer.switchCamera(object : CameraVideoCapturer.CameraSwitchHandler {
                    override fun onCameraSwitchDone(isFrontFacing: Boolean) {
                        onDone(isFrontFacing)
                    }

                    override fun onCameraSwitchError(error: String?) {
                        onError(error)
                    }
                })
            }
        }
    }

    fun setLocalAudioEnabled(isEnabled: Boolean): Boolean {
        return executor.submit(Callable {
            options.isLocalAudioEnabled = isEnabled
            localAudioTrack?.setEnabled(options.isLocalAudioEnabled)
        }).get() == true
    }

    fun setRemoteAudioEnabled(isEnabled: Boolean): Boolean {
        return executor.submit(Callable {
            options.isRemoteAudioEnabled = isEnabled
            remoteAudioTrack?.setEnabled(options.isRemoteAudioEnabled)
        }).get() == true
    }

    fun setLocalVideoEnabled(isEnabled: Boolean): Boolean {
        return executor.submit(Callable {
            options.isLocalVideoEnabled = isEnabled
            localVideoTrack?.setEnabled(options.isLocalVideoEnabled)
        }).get() == true
    }

    fun setRemoteVideoEnabled(isEnabled: Boolean): Boolean {
        return executor.submit(Callable {
            options.isRemoteVideoEnabled = isEnabled
            remoteVideoTrack?.setEnabled(options.isRemoteVideoEnabled)
        }).get() == true
    }

    fun addStream(mediaStream: MediaStream): Boolean {
        return peerConnection?.addStream(mediaStream) == true
    }

    fun removeStream(mediaStream: MediaStream) {
        peerConnection?.removeStream(mediaStream)
    }

    fun removeMediaStreamTrack(mediaStreamTrack: MediaStreamTrack): Boolean {
        return when {
            mediaStreamTrack.kind() == MediaStreamTrack.AUDIO_TRACK_KIND -> {
                remoteMediaStream?.removeTrack(remoteAudioTrack) == true
            }
            mediaStreamTrack.kind() == MediaStreamTrack.VIDEO_TRACK_KIND -> {
                remoteMediaStream?.removeTrack(remoteVideoTrack) == true
            }
            else -> {
                false
            }
        }
    }

    fun setLocalAudioTrackVolume(volume: Double) {
        localAudioTrack?.setVolume(volume)
    }

    fun setRemoteAudioTrackVolume(volume: Double) {
        remoteAudioTrack?.setVolume(volume)
    }

    fun setLocalVideoResolutionWidth(width: Int): Boolean {
        options.localVideoWidth = width
        return options.localVideoWidth == width
    }

    fun setLocalVideoResolutionHeight(height: Int): Boolean {
        options.localVideoHeight = height
        return options.localVideoHeight == height
    }

    fun isHDLocalVideo(): Boolean =
        options.isLocalVideoEnabled &&
                options.localVideoWidth * options.localVideoHeight >= 1280 * 720

    fun setLocalTextureSize(textureWidth: Int, textureHeight: Int) {
        surfaceTextureHelper?.setTextureSize(textureWidth, textureHeight)
    }

    fun changeCaptureFormat(width: Int, height: Int, fps: Int) {
        executor.execute {
            changeCaptureFormatInternal(width, height, fps)
        }
    }

    private fun changeCaptureFormatInternal(width: Int, height: Int, fps: Int) {
        Logger.debug(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + fps)
        localVideoSource?.adaptOutputFormat(width, height, fps)
    }

    fun setVideoSinks(local: Target, remote: Target) {
        Logger.debug(TAG, "setVideoSinks() -> local: $local, remote: $remote")

        localVideoSinkTarget = setLocalVideoSink(local)
        remoteVideoSinkTarget = setRemoteVideoSink(remote)
    }

    fun setLocalVideoSink(target: Target): Target? {
        val set = when (target) {
            Target.LOCAL -> localVideoSink?.setTarget(localSurfaceViewRenderer)
            Target.REMOTE -> localVideoSink?.setTarget(remoteSurfaceViewRenderer)
        }
        return if (set == true) target else null
    }

    fun setRemoteVideoSink(target: Target): Target? {
        val set = when (target) {
            Target.LOCAL -> remoteVideoSink?.setTarget(localSurfaceViewRenderer)
            Target.REMOTE -> remoteVideoSink?.setTarget(remoteSurfaceViewRenderer)
        }
        return if (set == true) target else null
    }

    fun setLocalVideoSink(
        surfaceViewRenderer: SurfaceViewRenderer,
        isMirrored: Boolean = false,
        isZOrderMediaOverlay: Boolean = true
    ): Boolean {
        localSurfaceViewRenderer = surfaceViewRenderer
        return if (initLocalCameraStream(isMirrored = isMirrored, isZOrderMediaOverlay = isZOrderMediaOverlay)) {
            localVideoSink?.setTarget(localSurfaceViewRenderer) == true
        } else {
            false
        }
    }

    fun setRemoteVideoSink(
        surfaceViewRenderer: SurfaceViewRenderer,
        isMirrored: Boolean = false,
        isZOrderMediaOverlay: Boolean = true
    ): Boolean {
        remoteSurfaceViewRenderer = surfaceViewRenderer
        return if (initRemoteCameraStream(isMirrored = isMirrored, isZOrderMediaOverlay = isZOrderMediaOverlay)) {
            remoteVideoSink?.setTarget(remoteSurfaceViewRenderer) == true
        } else {
            false
        }
    }

    fun getLocalVideoSinkTarget(): Target? = localVideoSinkTarget

    fun getRemoteVideoSinkTarget(): Target? = remoteVideoSinkTarget

    fun pauseLocalVideoStream() {
        localSurfaceViewRenderer?.pauseVideo()
    }

    fun pauseRemoteVideoStream() {
        remoteSurfaceViewRenderer?.pauseVideo()
    }

    fun startLocalVideoCapture() {
        executor.execute {
            localVideoCapturer?.startCapture(
                options.localVideoWidth,
                options.localVideoHeight,
                options.localVideoFPS
            )
        }
    }

    fun stopLocalVideoCapture() {
        executor.execute {
            localVideoCapturer?.stopCapture()
        }
    }

    fun setLocalVideoStreamMirror(isMirrored: Boolean) {
        executor.execute {
            localSurfaceViewRenderer?.setMirror(isMirrored)
        }
    }

    fun setRemoteVideoStreamMirror(isMirrored: Boolean) {
        executor.execute {
            remoteSurfaceViewRenderer?.setMirror(isMirrored)
        }
    }

    fun getAudioOutputDevices(): Set<RTCAudioManager.AudioDevice>? =
        audioManager?.getAudioDevices()

    fun getSelectedAudioOutputDevice(): RTCAudioManager.AudioDevice? =
        audioManager?.getSelectedAudioDevice()

    fun selectAudioOutputSpeakerPhone() {
        selectAudioDeviceInternally(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
    }

    fun selectAudioOutputEarpiece() {
        selectAudioDeviceInternally(RTCAudioManager.AudioDevice.EARPIECE)
    }

    private fun selectAudioDeviceInternally(audioDevice: RTCAudioManager.AudioDevice) {
        runOnUiThread {
            audioManager?.selectAudioDevice(audioDevice)
        }
    }

    fun removeListeners() {
        listener = null
    }

    fun dispose(): Boolean {
        if (executor.isShutdown) return false

        audioDeviceModule?.release()
        audioDeviceModule = null

        runOnUiThread {
            audioManager?.stop()
            audioManager = null

            Logger.debug(TAG, "Stopping capture")
            try {
                localVideoCapturer?.stopCapture()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        isInitiator = false

        audioBooleanConstraints.clearAll()
        audioIntegerConstraints.clearAll()
        peerConnectionConstraints.clearAll()
        offerAnswerConstraints.clearAll()

        localSessionDescription = null

        options = Options()

        remoteVideoScalingType = null

        localVideoSender = null

        executor.execute {
            localVideoSink?.setTarget(null)
            localVideoSink = null

            remoteVideoSink?.setTarget(null)
            remoteVideoSink = null

            try {
                localVideoCapturer?.dispose()
                localVideoCapturer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

            localVideoSource?.dispose()
            localVideoSource = null

            localAudioSource?.dispose()
            localAudioSource = null

            surfaceTextureHelper?.dispose()
            surfaceTextureHelper = null

            localMediaStream = null
            remoteMediaStream = null

//            try {
//                peerConnectionFactory?.stopAecDump()
//            } catch (e: IllegalStateException) {
//                e.printStackTrace()
//            }

            try {
                localSurfaceViewRenderer?.release()
            } catch (e: Exception) {
                Logger.debug(TAG, "Exception on SurfaceViewRenderer release. $e")
            } finally {
                localSurfaceViewRenderer = null
            }

            try {
                remoteSurfaceViewRenderer?.release()
            } catch (e: Exception) {
                Logger.debug(TAG, "Exception on SurfaceViewRenderer release. $e")
            } finally {
                remoteSurfaceViewRenderer = null
            }

            eglBase?.release()
            eglBase = null

//            try {
//                localVideoTrack?.dispose()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                localVideoTrack = null
//            }
            localVideoTrack = null

//            try {
//                remoteVideoTrack?.dispose()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                remoteVideoTrack = null
//            }
            remoteVideoTrack = null

//            try {
//                localAudioTrack?.dispose()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                localAudioTrack = null
//            }
            localAudioTrack = null

//            try {
//                remoteAudioTrack?.dispose()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                remoteAudioTrack = null
//            }
            remoteAudioTrack = null

            Logger.debug(TAG, "Closing peer connection")
            peerConnection?.dispose()
            peerConnection = null
            Logger.debug(TAG, "Closing peer connection done")

            Logger.debug(TAG, "Closing peer connection factory")
            peerConnectionFactory?.dispose()
            peerConnectionFactory = null
            Logger.debug(TAG, "Closing peer connection factory done")

//            try {
//                PeerConnectionFactory.stopInternalTracingCapture()
//                PeerConnectionFactory.shutdownInternalTracer()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }

        executor.shutdown()

        return true
    }

    private fun reportError(errorMessage: String) {
        Logger.error(TAG, "PeerConnection error: $errorMessage")
        executor.execute {
            listener?.onPeerConnectionError(errorMessage)
        }
    }

    private inner class InnerSdpObserver : SdpObserver {

        override fun onCreateSuccess(sessionDescription: SessionDescription?) {
            Logger.debug(TAG, "onCreateSuccess: $sessionDescription")

            if (sessionDescription == null) return

            if (localSessionDescription != null) {
                reportError("Multiple SDP create.")
                return
            }

            var sdpDescription = CodecUtils.preferCodec(
                sessionDescription.description,
                CodecUtils.AUDIO_CODEC_OPUS,
                true
            )

            sdpDescription = CodecUtils.preferCodec(
                sdpDescription,
                CodecUtils.VIDEO_CODEC_VP9,
                false
            )

            localSessionDescription = SessionDescription(sessionDescription.type, sdpDescription)

            executor.execute {
                Logger.debug(TAG, "Set local SDP from " + localSessionDescription?.type)
                peerConnection?.setLocalDescription(sdpObserver, localSessionDescription)
            }
        }

        override fun onSetSuccess() {
            Logger.debug(TAG, "onSetSuccess")

            executor.execute {
                if (isInitiator) {
                    // For offering peer connection we first create offer and set
                    // local SDP, then after receiving answer set remote SDP.
                    if (peerConnection?.remoteDescription == null) {
                        // We've just set our local SDP so time to send it.
                        Logger.debug(TAG, "Local SDP set successfully")
                        localSessionDescription?.let {
                            listener?.onLocalSessionDescription(SessionDescriptionMapper.map(it))
                        }
                    } else {
                        // We've just set remote description, so drain remote
                        // and send local ICE candidates.
                        Logger.debug(TAG, "Remote SDP set successfully")
                    }
                } else {
                    // For answering peer connection we set remote SDP and then
                    // create answer and set local SDP.
                    if (peerConnection?.localDescription != null) {
                        // We've just set our local SDP so time to send it, drain
                        // remote and send local ICE candidates.
                        Logger.debug(TAG, "Local SDP set successfully")
                        localSessionDescription?.let {
                            listener?.onLocalSessionDescription(SessionDescriptionMapper.map(it))
                        }
                    } else {
                        // We've just set remote SDP - do nothing for now -
                        // answer will be created soon.
                        Logger.debug(TAG, "Remote SDP set successfully")
                    }
                }
            }
        }

        override fun onCreateFailure(error: String?) {
            Logger.debug(TAG, "onCreateFailure: $error")

            reportError("Create SDP error: $error")
        }

        override fun onSetFailure(error: String?) {
            Logger.debug(TAG, "onSetFailure: $error")

            reportError("Set SDP error: $error")
        }
    }

    private inner class CameraEventsHandler : CameraVideoCapturer.CameraEventsHandler {

        override fun onCameraOpening(cameraName: String?) {
            Logger.debug(TAG, "onCameraOpening() -> cameraName: $cameraName")
            cameraBehaviorListener?.onCameraOpening(cameraName)
        }

        override fun onFirstFrameAvailable() {
            Logger.debug(TAG, "onFirstFrameAvailable()")
            cameraBehaviorListener?.onFirstFrameAvailable()
        }

        override fun onCameraFreezed(errorDescription: String?) {
            Logger.debug(TAG, "onCameraFreezed() -> errorDescription: $errorDescription")
            cameraBehaviorListener?.onCameraFreezed(errorDescription)
        }

        override fun onCameraDisconnected() {
            Logger.debug(TAG, "onCameraDisconnected()")
            cameraBehaviorListener?.onCameraDisconnected()
        }

        override fun onCameraClosed() {
            Logger.debug(TAG, "onCameraClosed()")
            cameraBehaviorListener?.onCameraClosed()
        }

        override fun onCameraError(errorDescription: String?) {
            Logger.debug(TAG, "onCameraError()")
            cameraBehaviorListener?.onCameraError(errorDescription)
        }

    }

    private fun runOnUiThread(action: Runnable) {
        try {
            val context = context
            if (context is Activity) {
                context.runOnUiThread(action)
            } else {
                uiThread.post(action)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAudioMediaConstraints(): MediaConstraints = MediaConstraints().apply {
        addConstraints(audioBooleanConstraints, audioIntegerConstraints)
    }

    private fun getPeerConnectionMediaConstraints(): MediaConstraints = MediaConstraints().apply {
        addConstraints(peerConnectionConstraints)
    }

    private fun getOfferAnswerConstraints(): MediaConstraints = MediaConstraints().apply {
        addConstraints(offerAnswerConstraints)
    }

    private fun getOfferAnswerRestartConstraints(): MediaConstraints = getOfferAnswerConstraints().apply {
        mandatory.add(OfferAnswerConstraints.ICE_RESTART.toKeyValuePair(true))
    }

    private fun MediaConstraints.addConstraints(constraints: RTCConstraints<*, *>): Boolean {
        return mandatory.addAll(constraints.mandatoryKeyValuePairs) && optional.addAll(constraints.optionalKeyValuePairs)
    }

    private fun MediaConstraints.addConstraints(vararg constraints: RTCConstraints<*, *>) {
        constraints.forEach { addConstraints(it) }
    }

    interface Listener {
        fun onLocalSessionDescription(
            sessionDescription: kz.inqbox.sdk.domain.model.webrtc.SessionDescription
        )
        fun onLocalIceCandidate(iceCandidate: kz.inqbox.sdk.domain.model.webrtc.IceCandidate)
        fun onIceConnectionChange(iceConnectionState: IceConnectionState)
        fun onRenegotiationNeeded()

        fun onAddRemoteStream(mediaStream: MediaStream)
        fun onRemoveStream(mediaStream: MediaStream)

        fun onLocalVideoCapturerCreateError(e: Exception)
        fun onPeerConnectionError(errorMessage: String)
    }

    interface CameraBehaviorListener {
        // Callback invoked when camera is opening
        fun onCameraOpening(cameraName: String?)

        // Callback invoked when first camera frame is available after camera is opened
        fun onFirstFrameAvailable()

        // Invoked when camera stops receiving frames
        fun onCameraFreezed(errorDescription: String?)

        // Camera error handler - invoked when camera can not be opened or any
        // camera exception happens on camera thread.
        fun onCameraError(errorDescription: String?)

        // Called when camera is disconnected
        fun onCameraDisconnected()

        // Callback invoked when camera closed
        fun onCameraClosed()
    }

}
