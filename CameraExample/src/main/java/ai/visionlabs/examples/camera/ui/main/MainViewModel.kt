package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.ui.Settings
import android.app.Activity
import android.util.Log
import android.widget.Toast
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
import ru.visionlabs.sdk.lunacore.Interactions
import ru.visionlabs.sdk.lunacore.LunaID

sealed class MainViewState {

    object Init : MainViewState()

    data class Error(val message: String?) : MainViewState()

    data class Image(val image: BestShot, val videoPath: String?) : MainViewState()

    data class Cancelled(val videoPath: String?) : MainViewState()
}

class MainViewModel : ViewModel() {

    private val mutableStateLiveData = MutableLiveData<MainViewState>(MainViewState.Init)
    val stateLiveData: LiveData<MainViewState> = mutableStateLiveData

    fun updateState(s: MainViewState) {
        mutableStateLiveData.postValue(s)
    }

    fun init(viewLifecycleOwner: LifecycleOwner) {
        Log.d("@@@@", "Main VM created")


        LunaID.finishStates()
            .map { it.result }
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is LunaID.FinishResult.ResultSuccess -> {
                        Log.d("@@@", "bestShot: ${it.data}")
                        updateState(
                            MainViewState.Image(
                                it.data.bestShot,
                                it.data.videoPath
                            )
                        )
                    }
                    is LunaID.FinishResult.ResultFailed -> {
                        val t = it.data

                        val m = when(t) {
                            is LunaID.FinishFailedData.InteractionFailed -> "Interaction failed"
                            is LunaID.FinishFailedData.LivenessCheckFailed -> "Liveness check failed (not live)"
                            is LunaID.FinishFailedData.LivenessCheckError -> "Liveness check error"
                            is LunaID.FinishFailedData.UnknownError -> "unrecognized error"
                            else -> "unrecognized error"
                        }

                        updateState(MainViewState.Error(m))
                    }
                    is LunaID.FinishResult.ResultCancelled -> {
                        updateState(MainViewState.Cancelled(it.data.videoPath))
                    }
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    override fun onCleared() {
        Log.d("@@@@", "Main VM cleared")
        super.onCleared()
    }

    fun onShowCameraWithDetectionClicked(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
            )
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Settings.overlayShowDetection = false
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
            )
        )
    }

    fun onShowCameraAndRecordVideo(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                recordVideo = true,
            )
        )
    }

    fun onShowCameraWithInteraction(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
            ),
            interactions = Interactions.Builder()
                .addInteraction(BlinkInteraction())
                .build()
        )
    }

    fun onShowCameraWithLiveness(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams()
        )
    }
}
