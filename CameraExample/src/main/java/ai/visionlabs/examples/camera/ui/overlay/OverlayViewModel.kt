package ai.visionlabs.examples.camera.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacamera.presentation.CameraOverlayDelegate
import ru.visionlabs.sdk.lunacamera.presentation.CameraOverlayDelegateOut

class OverlayViewModel(
    val overlayDelegate: CameraOverlayDelegateOut
) : ViewModel() {

    init {
        Log.d("@@@@", "overlay VM created")
    }

    override fun onCleared() {
        Log.d("@@@@", "overlay VM cleared")
        super.onCleared()
    }

}