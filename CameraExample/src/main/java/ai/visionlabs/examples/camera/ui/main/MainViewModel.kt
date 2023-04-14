package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.ui.Settings
import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacamera.presentation.CameraUIDelegate
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.CameraState
import ru.visionlabs.sdk.lunacore.LunaError
import ru.visionlabs.sdk.lunacore.LunaID

sealed class MainViewState {

    object Init : MainViewState()

    class Error(val message: String?) : MainViewState()

    class Image(val image: BestShot) : MainViewState()

}

class MainViewModel : ViewModel() {

    private val mutableStateLiveData = MutableLiveData<MainViewState>(MainViewState.Init)
    val stateLiveData: LiveData<MainViewState> = mutableStateLiveData

    private val cameraUIDelegate = object : CameraUIDelegate {
        override fun bestShot(bestShot: BestShot): Boolean {
            Log.d("@@@", "bestShot: $bestShot")
            updateState(MainViewState.Image(bestShot))
            bestShot.descriptor
            return true
        }

        override fun canceled() {
            Log.d("@@@", "cancelled")
        }

        override fun error(error: LunaError) {
            Log.d("@@@", "error: $error")
            updateState(MainViewState.Error(error.message))
        }
    }

    fun updateState(s: MainViewState) {
        mutableStateLiveData.postValue(s)
    }

    init {
        Log.d("@@@@", "Main VM created")
        val s = LunaID.popLastCameraState()
        when (s) {
            is CameraState.BestshotFound -> {
                updateState(MainViewState.Image(s.bestshot))
            }
            else -> {
                // noop
            }
        }
    }

    override fun onCleared() {
        Log.d("@@@@", "Main VM cleared")
        LunaID.unregisterListener(cameraUIDelegate)
        super.onCleared()
    }

    fun onShowCameraWithDetectionClicked(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaID.showCamera(
            activity,
            cameraUIDelegate,
            disableErrors = true,
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Settings.overlayShowDetection = false
        LunaID.showCamera(
            activity,
            cameraUIDelegate,
            disableErrors = true,
        )
    }
}
