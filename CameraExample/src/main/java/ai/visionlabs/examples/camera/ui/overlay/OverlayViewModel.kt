package ai.visionlabs.examples.camera.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel

class OverlayViewModel(
) : ViewModel() {

    init {
        Log.d("OverlayViewModel", "init()")
    }

    override fun onCleared() {
        Log.d("OverlayViewModel", "onCleared()")
        super.onCleared()
    }

}