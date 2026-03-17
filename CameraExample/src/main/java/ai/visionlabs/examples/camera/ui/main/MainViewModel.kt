package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.R
import ai.visionlabs.examples.camera.ui.Settings
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.BlinkInteraction
import ru.visionlabs.sdk.lunacore.CloseCameraCommand
import ru.visionlabs.sdk.lunacore.Commands
import ru.visionlabs.sdk.lunacore.Interactions
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.PitchDownInteraction
import ru.visionlabs.sdk.lunacore.PitchUpInteraction
import ru.visionlabs.sdk.lunacore.ShowCameraParams
import ru.visionlabs.sdk.lunacore.StartBestShotSearchCommand
import ru.visionlabs.sdk.lunacore.YawLeftInteraction
import ru.visionlabs.sdk.lunacore.YawRightInteraction
import ru.visionlabs.sdk.lunacore.borderdistances.BorderDistancesStrategy
import ru.visionlabs.videocompressor.VideoCompressor
import java.io.File

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

    fun init(viewLifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "init() Main VM created")
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared()")
        super.onCleared()
    }

    fun onShowCamera(activity: Activity) {
        Log.d(TAG, "onShowCameraWithDetectionClicked()")
        Settings.isFaceZoneVisible = false

        LunaID.showCamera(
            activity,
            ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = BorderDistancesStrategy.WithDp(bottomPaddingInDp = 20, leftPaddingInDp = 20, rightPaddingInDp = 20, topPaddingInDp = 20),
                recordVideo = Settings.recordVideo,
                recordingTimeMillis = 10000,
            )
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Log.d(TAG, "onShowCameraWithFrameClicked()")
        Settings.isFaceZoneVisible = true
        LunaID.showCamera(
            activity,
            ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = BorderDistancesStrategy.WithViewId(R.id.faceZone),
                recordVideo = Settings.recordVideo,
                recordingTimeMillis = 10000
            )
        )
    }

    fun onShowCameraWithInteraction(activity: Activity) {
        Log.d(TAG, "onShowCameraWithInteraction()")
        Settings.isFaceZoneVisible = false
        LunaID.showCamera(
            activity,
            ShowCameraParams(
                disableErrors = true,
                recordVideo = Settings.recordVideo,
                recordingTimeMillis = 10000
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
            ShowCameraParams(
                disableErrors = false,
                recordVideo = Settings.recordVideo,
                recordingTimeMillis = 10000
            ),
            commands = commands,
        )
    }

    private fun setupStartDelay() {
        val startDelayMs = 5_000L
        handler?.postDelayed(
            {
                Log.d(TAG, "StartBestShotSearchCommand()")
                LunaID.sendCommand(StartBestShotSearchCommand)
            }, startDelayMs
        )
    }

    suspend fun saveVideo(context: Context, path: String, shouldCompress: Boolean) {
        val inputFile = File(path)
        if (shouldCompress){
            val nameCompressed = "best_shots/compressed_video.mp4"
            val outputCompressedFile = File(context.filesDir, nameCompressed)
            VideoCompressor.compress(inputFile, outputCompressedFile,250000).collect{
                when(it){
                    is VideoCompressor.State.Success -> {
                        Log.i("saveVideo", "Compressed video has been saved: $nameCompressed")
                    }
                    is VideoCompressor.State.Failed -> {
                        Log.e("saveVideo", "Compressed video saving has failed", it.exception)
                    }
                    VideoCompressor.State.Cancelled ->
                        Log.e("saveVideo", "Compressed video saving has been cancelled")
                }
            }
        } else {
            val nameOriginal = "best_shots/original_video.mp4"
            val outputOriginalFile = File(context.filesDir, nameOriginal)
            inputFile.copyTo(outputOriginalFile, overwrite = true)
            Log.i("saveVideo", "Original video has been saved: $nameOriginal")
        }
    }

    companion object{
        private const val TAG = "MainViewModel"
    }
}
