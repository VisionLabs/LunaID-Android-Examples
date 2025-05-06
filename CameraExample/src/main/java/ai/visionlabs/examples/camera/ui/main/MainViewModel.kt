package ai.visionlabs.examples.camera.ui.main

import android.app.Activity
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.BlinkInteraction
import ru.visionlabs.sdk.lunacore.Commands
import ru.visionlabs.sdk.lunacore.Interactions
import ru.visionlabs.sdk.lunacore.LunaAspectRatioStrategy
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.PitchDownInteraction
import ru.visionlabs.sdk.lunacore.PitchUpInteraction
import ru.visionlabs.sdk.lunacore.YawLeftInteraction
import ru.visionlabs.sdk.lunacore.YawRightInteraction
import ru.visionlabs.sdk.lunacore.borderdistances.BorderDistancesStrategy
import ai.visionlabs.examples.camera.R
import ai.visionlabs.examples.camera.ui.Settings

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

    fun onShowCameraWithDetectionClicked(activity: Activity) {
        Log.d(TAG, "onShowCameraWithDetectionClicked()")
        Settings.isFaceZoneVisible = false

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = BorderDistancesStrategy.WithDp(bottomPaddingInDp = 20, leftPaddingInDp = 20, rightPaddingInDp = 20, topPaddingInDp = 20),
                recordVideo = true,
                recordingTimeMillis = 10000,
                checkSecurity = true,
                preferredAnalysisFrameWidth = 720,
                preferredAnalysisFrameHeight =720 ,
                aspectRatioStrategy = LunaAspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
            )
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Log.d(TAG, "onShowCameraWithFrameClicked()")
        Settings.isFaceZoneVisible = true
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = BorderDistancesStrategy.WithViewId(R.id.faceZone),
                recordVideo = true,
                recordingTimeMillis = 10000,
                checkSecurity = true
            )
        )
    }

    fun onShowCameraAndRecordVideo(activity: Activity) {
        Log.d(TAG, "onShowCameraAndRecordVideo()")
        Settings.isFaceZoneVisible = false
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                recordVideo = true,
                recordingTimeMillis = 10000,
                preferredAnalysisFrameWidth = 720,
                preferredAnalysisFrameHeight =720 ,
                aspectRatioStrategy = LunaAspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY,
            )
        )
    }

    fun onShowCameraWithInteraction(activity: Activity) {
        Log.d(TAG, "onShowCameraWithInteraction()")
        Settings.isFaceZoneVisible = false
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                recordVideo = true,
                recordingTimeMillis = 5000,
                preferredAnalysisFrameHeight = 1200,
                preferredAnalysisFrameWidth = 1200,
                aspectRatioStrategy = LunaAspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
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

    companion object{
        private const val TAG = "MainViewModel"
    }
}
