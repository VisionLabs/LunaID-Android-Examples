package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.ui.Settings
import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacamera.presentation.CameraUIDelegate
import ru.visionlabs.sdk.lunacamera.presentation.LunaCameraBuilder
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.LunaError

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

    fun onShowCameraWithDetectionClicked(activity: Activity) {
        Settings.overlayShowDetection = true
        LunaCameraBuilder.showCamera(
            activity,
            cameraUIDelegate,
            disableErrors = true,
        )
    }

    fun onShowCameraWithFrameClicked(activity: Activity) {
        Settings.overlayShowDetection = false
        LunaCameraBuilder.showCamera(
            activity,
            cameraUIDelegate,
            disableErrors = true,
        )
    }
}
