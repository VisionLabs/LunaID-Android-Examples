package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.R
import ai.visionlabs.examples.camera.ui.Settings
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.BlinkInteraction
import ru.visionlabs.sdk.lunacore.CloseCameraCommand
import ru.visionlabs.sdk.lunacore.Commands
import ru.visionlabs.sdk.lunacore.Interactions
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.PitchDownInteraction
import ru.visionlabs.sdk.lunacore.PitchUpInteraction
import ru.visionlabs.sdk.lunacore.StartBestShotSearchCommand
import ru.visionlabs.sdk.lunacore.YawLeftInteraction
import ru.visionlabs.sdk.lunacore.YawRightInteraction
import ru.visionlabs.sdk.lunacore.borderdistances.InitBorderDistancesStrategy

sealed class MainViewState {

    object Init : MainViewState()

    data class Error(val message: String?) : MainViewState()

    data class Image(val image: BestShot, val videoPath: String?) : MainViewState()

    data class Cancelled(val videoPath: String?) : MainViewState()
}

class MainViewModel : ViewModel() {

    private val mutableStateLiveData = MutableLiveData<MainViewState>(MainViewState.Init)
    val stateLiveData: LiveData<MainViewState> = mutableStateLiveData


    private var handler: Handler? = null
    private var commands = Commands.Builder().build()

//    private var videoQuality = TextureMovieEncoder.VideoQuality.LOW


    fun updateState(s: MainViewState) {
        mutableStateLiveData.postValue(s)
    }

    fun init(viewLifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "init() Main VM created")


        LunaID.finishStates()
            .map { it.result }
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                Log.d(TAG, "init() collect finish state: $it")
                when (it) {
                    is LunaID.FinishResult.ResultSuccess -> {
                        Log.d(TAG, "init() ResultSuccess, bestShot: ${it.data}")

                        if (Settings.commandsOverridden && commands.isCloseOverridden()) {
                            processSuccessWithDelay(it)
                        } else {
                            processSuccessImmediately(it)
                        }
                    }

                    is LunaID.FinishResult.ResultFailed -> {
                        Log.d(TAG, "init() ResultFailed")

                        val t = it.data

                        val m = when (t) {
                            is LunaID.FinishFailedData.InteractionFailed -> "Interaction failed"
                            is LunaID.FinishFailedData.LivenessCheckFailed -> "Liveness check failed (not live)"
                            is LunaID.FinishFailedData.LivenessCheckError -> "Liveness check error"
                            is LunaID.FinishFailedData.UnknownError -> "unrecognized error"
                            else -> "unrecognized error"
                        }

                        updateState(MainViewState.Error(m))
                    }

                    is LunaID.FinishResult.ResultCancelled -> {
                        Log.d(TAG, "init() ResultCancelled")

                        updateState(MainViewState.Cancelled(it.data.videoPath))
                    }

                    else -> {}
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun processSuccessImmediately(result: LunaID.FinishResult.ResultSuccess) {
        Log.d(TAG, "processSuccessImmediately()")
        updateState(MainViewState.Image(result.data.bestShot,result.data.videoPath))
    }

    private fun processSuccessWithDelay(result: LunaID.FinishResult.ResultSuccess) {
        val finishDelayMs = 5_000L
        Log.d(TAG, "processSuccessWithDelay() delay: $finishDelayMs")

        handler?.removeCallbacksAndMessages(null)
        handler?.postDelayed(
            {
                LunaID.sendCommand(CloseCameraCommand)
                updateState(MainViewState.Image(result.data.bestShot,result.data.videoPath))

            }, finishDelayMs
        )
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared()")
        super.onCleared()
    }

    fun onShowCameraWithDetectionClicked(activity: Activity) {
        Log.d(TAG, "onShowCameraWithDetectionClicked()")

        Settings.overlayShowDetection = true
        Settings.commandsOverridden = false
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = InitBorderDistancesStrategy.WithDp(bottomPaddingInDp = 10)
            )
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Log.d(TAG, "onShowCameraWithFrameClicked()")

        Settings.overlayShowDetection = false
        Settings.commandsOverridden = false

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
            )
        )
    }

    fun onShowCameraAndRecordVideo(activity: Activity) {
        Log.d(TAG, "onShowCameraAndRecordVideo()")

        Settings.overlayShowDetection = true
        Settings.commandsOverridden = false

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                recordVideo = true,
                recordingTimeMillis = 10000
            )
        )
    }

    fun onShowCameraWithInteraction(activity: Activity) {
        Log.d(TAG, "onShowCameraWithInteraction()")

        Settings.overlayShowDetection = true
        Settings.commandsOverridden = false

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
            ),
            interactions = Interactions.Builder()
                .addInteraction(BlinkInteraction(timeoutMs = 30_000, acceptOneEyed = true))
                .addInteraction(
                    YawLeftInteraction(
                        timeoutMs = 30_000,
                        startAngleDeg = 15,
                        endAngleDeg = 20
                    )
                )
                .addInteraction(
                    YawRightInteraction(
                        timeoutMs = 30_000,
                        startAngleDeg = 15,
                        endAngleDeg = 20
                    )
                )
                .addInteraction(
                    PitchUpInteraction(
                        timeoutMs = 30_000,
                        startAngleDeg = 10,
                        endAngleDeg = 15
                    )
                )
                .addInteraction(
                    PitchDownInteraction(
                        timeoutMs = 30_000,
                        startAngleDeg = 10,
                        endAngleDeg = 15
                    )
                )
                .build()
        )
    }

    fun onShowCameraWithCommands(
        activity: Activity,
        overrideStart: Boolean,
        overrideClose: Boolean
    ) {
        Log.d(TAG, "onShowCameraWithCommands()")

        Settings.overlayShowDetection = true
        Settings.commandsOverridden = true

        handler?.removeCallbacksAndMessages(null)
        handler = Handler(Looper.getMainLooper())

        commands = Commands.Builder().apply {
            if (overrideStart) override(StartBestShotSearchCommand)
            if (overrideClose) override(CloseCameraCommand)
        }.build()

        if (commands.isStartOverridden()) {
            setupStartDelay()
        }

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = false,
                borderDistanceStrategy = InitBorderDistancesStrategy.WithViewId(R.id.faceZone)
            ),
            commands = commands,
        )
    }

    fun onChangeVideoQuality(isChecked: Boolean) {
//        videoQuality =
//            if (isChecked) TextureMovieEncoder.VideoQuality.HIGH else TextureMovieEncoder.VideoQuality.LOW
    }

    private fun setupStartDelay() {
        val startDelayMs = 3_000L
        handler?.postDelayed(
            {
                LunaID.sendCommand(StartBestShotSearchCommand)
            }, startDelayMs
        )
    }

    companion object{
        private const val TAG = "MainViewModel"
    }
}
