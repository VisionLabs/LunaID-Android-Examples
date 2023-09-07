package ai.visionlabs.examples.camera.ui.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OverlayViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = OverlayViewModel()
        return vm as T
    }
}