package ai.visionlabs.examples.camera.ui.settings

import ai.visionlabs.examples.camera.ui.overlay.OverlayViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = SettingsViewModel()
        return vm as T
    }
}