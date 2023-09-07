package ai.visionlabs.examples.camera.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel

class OverlayViewModel(
) : ViewModel() {

    init {
        Log.d("@@@@", "overlay VM created")
    }

    override fun onCleared() {
        Log.d("@@@@", "overlay VM cleared")
        super.onCleared()
    }

}