package ai.visionlabs.examples.camera.ui.matching

import android.app.Activity
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.visionlabs.sdk.lunacore.BestShot
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.borderdistances.BorderDistancesStrategy
import ru.visionlabs.sdk.lunacore.utils.LunaUtils
import ru.visionlabs.sdk.lunacore.utils.LunaUtils.V60

sealed class MatchingFacesState {

    object Init : MatchingFacesState()

    object InProgress : MatchingFacesState()

    data class BestShotsResult(val firstImage: Bitmap?, val secondImage: Bitmap?) :
        MatchingFacesState()

    data class MatchingResult(val firstImage: Bitmap, val secondImage: Bitmap, val score: Float) :
        MatchingFacesState()

    data class Error(val message: String?) : MatchingFacesState()

    fun toBestShotsResult(): BestShotsResult =
        if (this is BestShotsResult) this else if (this is MatchingResult)
            BestShotsResult(firstImage, secondImage) else BestShotsResult(null, null)

}

class MatchingFacesViewModel : ViewModel() {

    private val _state: MutableStateFlow<MatchingFacesState> =
        MutableStateFlow(MatchingFacesState.Init)
    val state = _state.asStateFlow()


    private var onBestShotFound: ((bestShot: BestShot) -> Unit)? = null

    init {
        viewModelScope.launch {
            LunaID.bestShot.collect {
                it?.let {
                    onBestShotFound?.invoke(it.bestShot)
                }
            }
        }
    }

    fun findFirstFace(activity: Activity) {
        onBestShotFound = { b ->
            viewModelScope.launch {
                val result = _state.value.toBestShotsResult().copy(firstImage = b.warp)
                _state.emit(result)
            }
        }
        showCamera(activity)
    }

    fun findSecondFace(activity: Activity) {
        onBestShotFound = { b ->
            viewModelScope.launch {
                val result = _state.value.toBestShotsResult().copy(secondImage = b.warp)
                _state.emit(result)
            }
        }
        showCamera(activity)
    }

    fun matchFaces(firstBitmap: Bitmap, secondBitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            _state.emit(MatchingFacesState.InProgress)
            try {
                val descriptorFirst =
                    LunaUtils.getDescriptor(firstBitmap, descriptorVersion = V60)
                val descriptorSecond =
                    LunaUtils.getDescriptor(secondBitmap, descriptorVersion = V60)
                val similarityScore = LunaUtils.matchDescriptors(
                    descriptorFirst,
                    descriptorSecond,
                    descriptorVersion = V60
                )
                _state.emit(
                    MatchingFacesState.MatchingResult(
                        firstImage = firstBitmap,
                        secondImage = secondBitmap,
                        score = similarityScore
                    )
                )
            } catch (t: Throwable) {
                _state.emit(MatchingFacesState.Error(t.message ?: "Unknown error"))
            }
        }
    }

    private fun showCamera(activity: Activity) {

        LunaID.showCamera(
            activity,
            LunaID.ShowCameraParams(
                disableErrors = true,
                borderDistanceStrategy = BorderDistancesStrategy.WithDp(
                    bottomPaddingInDp = 20,
                    leftPaddingInDp = 20,
                    rightPaddingInDp = 20,
                    topPaddingInDp = 20
                )
            )
        )
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}