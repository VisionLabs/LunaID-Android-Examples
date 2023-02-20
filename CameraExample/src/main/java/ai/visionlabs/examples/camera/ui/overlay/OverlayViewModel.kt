package ai.visionlabs.examples.camera.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.visionlabs.sdk.lunacamera.presentation.CameraOverlayDelegate

class OverlayViewModel(
    val overlayDelegate: CameraOverlayDelegate
) : ViewModel() {

    init {
        Log.d("@@@@", "overlay VM created")
    }

    override fun onCleared() {
        Log.d("@@@@", "overlay VM cleared")
        super.onCleared()
    }

}