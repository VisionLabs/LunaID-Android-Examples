package ai.visionlabs.examples.camera.ui.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.visionlabs.sdk.lunacamera.presentation.LunaCameraBuilder

class OverlayViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = OverlayViewModel(
            overlayDelegate = LunaCameraBuilder.cameraOverlayDelegate
        )
        return vm as T
    }
}