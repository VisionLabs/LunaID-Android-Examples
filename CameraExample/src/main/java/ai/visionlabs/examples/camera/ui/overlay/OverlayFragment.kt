package ai.visionlabs.examples.camera.ui.overlay

import ai.visionlabs.examples.camera.databinding.FragmentOverlayBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.sample
import ru.visionlabs.sdk.lunacore.Interaction
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.LunaInteractionType
import ru.visionlabs.sdk.lunacore.faceengine.DetectionError
import ru.visionlabs.sdk.lunacore.faceengine.messageResId

class OverlayFragment : Fragment() {

    private val viewModel: OverlayViewModel by viewModels()

    private var _binding: FragmentOverlayBinding? = null
    private val binding get() = _binding!!


    companion object{
        private const val TAG = "OverlayFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverlayBinding.inflate(inflater, container, false)
        with (binding) {
            overlayClose.setOnClickListener { requireActivity().finish() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        binding.overlayViewport.isVisible = true
        binding.overlayDetection.isVisible = false
        binding.overlayLegend.isVisible = false
        binding.faceZone.isVisible = false


        LunaID.currentInteractionType
            .filterNotNull()
            .onEach {
                Log.d(TAG,"onViewCreated: collected interaction $it")
                _binding?.overlayInteraction?.text = getInteractionTip(it)
            }.flowOn(Dispatchers.Main)
            .launchIn(lifecycleScope)

        LunaID.eventChannel.receiveAsFlow()
            .onEach {
                when(it){
                    is LunaID.Event.SecurityCheck.Success -> {
                        Log.d(TAG, "onViewCreated() collect security SUCCESS")
                    }
                    is LunaID.Event.SecurityCheck.Failure -> {
                        Log.d(TAG, "onViewCreated() collect security FAILURE")
                    }
                    is LunaID.Event.FaceFound -> {
                        Log.d(TAG, "onViewCreated() face found")
                    }
                    is LunaID.Event.InteractionEnded -> {
                        Log.d(TAG, "onViewCreated() interaction ended")
                    }
                    is LunaID.Event.InteractionFailed -> {
                        Log.d(TAG, "onViewCreated() interaction failed")
                    }
                    is LunaID.Event.InteractionTimeout -> {
                        Log.d(TAG, "onViewCreated() interaction timeout")
                    }
                    is LunaID.Event.LivenessCheckError -> {
                        Log.d(TAG, "onViewCreated() liveness check error ${it.cause}")
                    }
                    is LunaID.Event.LivenessCheckFailed -> {
                        Log.d(TAG, "onViewCreated() liveness check failed")
                    }
                    is LunaID.Event.LivenessCheckStarted -> {
                        Log.d(TAG, "onViewCreated() liveness check started")
                    }
                    is LunaID.Event.Started -> {
                        Log.d(TAG, "onViewCreated() started")
                    }
                    is LunaID.Event.UnknownError -> {
                        Log.d(TAG, "onViewCreated() unknown error ${it.cause}")
                    }
                    else -> { Log.d(TAG,"onViewCreated() collected unknown event") }
                }
            }
            .launchIn(this.lifecycleScope)

        LunaID.errorChannel.receiveAsFlow()
            .sample(1000)
            .onEach {
                binding.overlayError.text = getString(it.error.messageResId()!!)
                delay(1000)
                binding.overlayError.text = ""
            }
            .launchIn(this.lifecycleScope)

        LunaID.faceDetectionChannel.receiveAsFlow()
            .onEach {
                Log.d(TAG, "onViewCreated() face detection event: $it")
            }.launchIn(this.lifecycleScope)
//
//        LunaID.detectionCoordinates()
//            .flowOn(Dispatchers.IO)
//            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach {
//                //todo
////                processDetectRect(it.data)
//            }
//            .flowOn(Dispatchers.Main)
//            .launchIn(viewLifecycleOwner.lifecycleScope)
//
//        LunaID.allEvents()
//            .flowOn(Dispatchers.IO)
//            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach {
//                Log.d(TAG, "onViewCreated() collect event: $it")
//            }
//            .flowOn(Dispatchers.Main)
//            .launchIn(viewLifecycleOwner.lifecycleScope)
//
//        LunaID.allEvents()
//            .filterIsInstance<LunaID.Event.InteractionStarted>()
//            .flowOn(Dispatchers.IO)
//            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach {
//                Log.d(TAG, "onViewCreated() collect interaction event: $it")
//                processInteractionEvent(it.type)
//            }
//            .flowOn(Dispatchers.Main)
//            .launchIn(viewLifecycleOwner.lifecycleScope)
//
//        LunaID.detectionErrors()
//            .flowOn(Dispatchers.IO)
//            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach {
//                Log.d(TAG, "onViewCreated() collect error event: $it")
//                processError(it.error)
//            }
//            .flowOn(Dispatchers.Main)
//            .launchIn(viewLifecycleOwner.lifecycleScope)
//
//        LunaID.allEvents()
//            .filterIsInstance<LunaID.Event.SecurityCheck>()
//            .flowOn(Dispatchers.IO)
//            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach {
//                Log.d(TAG, "onViewCreated() collect security check event: $it")
//
//                if(it is LunaID.Event.SecurityCheck.Success){
//                    Toast.makeText(this.activity, "Security validation success", Toast.LENGTH_SHORT).show()
//                }else if(it is LunaID.Event.SecurityCheck.Failure){
//                    Toast.makeText(this.activity, "Security validation failed", Toast.LENGTH_SHORT).show()
//                    requireActivity().finish()
//                }
//            }.flowOn(Dispatchers.Main)
//            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun processInteractionEvent(@LunaInteractionType type: Int) {
        val text = getInteractionTip(type)

//        interactionTipsHandler.post {
//            interactionTipsHandler.removeCallbacksAndMessages(null)
//            binding.overlayInteraction.text = text
//        }
//        interactionTipsHandler.postDelayed(1000L) {
//            binding.overlayError.text = ""
//        }
    }


    private fun getInteractionTip(@LunaInteractionType interactionType: Int): String {
        return when (interactionType) {
            Interaction.BLINK ->  {
                "please blink"
            }
            Interaction.YAW_LEFT -> {
                "please yaw left"
            }
            Interaction.YAW_RIGHT -> {
                "please yaw right"
            }
            Interaction.PITCH_UP -> {
                "please pitch up"
            }
            Interaction.PITCH_DOWN -> {
                "please pitch down"
            }
            else -> throw IllegalArgumentException("unrecognized interaction type: $interactionType")
        }
    }

    /**
     * here we are enabling preview for various [LunaConfig] params
     * related to detection borders.
     * namely:
     * [LunaConfig.borderDistanceLeft]
     * [LunaConfig.borderDistanceTop]
     * [LunaConfig.borderDistanceRight]
     * [LunaConfig.borderDistanceBottom]
     * [LunaConfig.detectFrameSize]
     *
     */
//    private fun processDetectRect(rect: RectF) {
//        if (!Settings.overlayShowDetection) return
//
//        val lc = App.lunaConfig
//        val bd = LunaID.borderDistances
//
//
//        val w = binding.overlayDetection.measuredWidth
//        val h = binding.overlayDetection.measuredHeight
////        val minFaceSideToMinScreenSide = lc.minFaceSideToMinScreenSide
////        val frameSize = if(w < h) w*minFaceSideToMinScreenSide else h*minFaceSideToMinScreenSide
//
//        val frameSizeRect = RectF(
//            0f, 0f, 350f, 350f,
////            0f, 0f, frameSize, frameSize,
//        ).apply {  offsetTo(rect.left, rect.top) }
//        val scaledFrameSizedRect = LunaUtils.scalePreviewRect(frameSizeRect)
//
//        val borderDistanceRect = if (w > 0 && h > 0) RectF(
//            scalePreviewDistance(bd.fromLeft),
//            scalePreviewDistance(bd.fromTop),
//            w - scalePreviewDistance(bd.fromRight),
//            h - scalePreviewDistance(bd.fromBottom),
//        ) else null
//
//        binding.overlayDetection.update(
//            faceDetectionRect = rect,
//            minFaceDetectionRect = scaledFrameSizedRect,
//            borderDistanceRect = borderDistanceRect,
//        )
//    }

    private fun processError(error: DetectionError) {
        val errorTextResId = error.messageResId() ?: return

//        errorShowHandler.post {
//            errorShowHandler.removeCallbacksAndMessages(null)
//            binding.overlayError.setText(errorTextResId)
//            errorShowHandler.postDelayed(1000L) {
//                binding.overlayError.text = ""
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        interactionTipsHandler.removeCallbacksAndMessages(null)
//        errorShowHandler.removeCallbacksAndMessages(null)
//        _binding = null
    }

}