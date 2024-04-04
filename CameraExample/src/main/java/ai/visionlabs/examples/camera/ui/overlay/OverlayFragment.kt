package ai.visionlabs.examples.camera.ui.overlay

import ai.visionlabs.examples.camera.App
import ai.visionlabs.examples.camera.databinding.FragmentOverlayBinding
import ai.visionlabs.examples.camera.ui.Settings
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.Interaction
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.LunaInteractionType
import ru.visionlabs.sdk.lunacore.faceengine.DetectionError
import ru.visionlabs.sdk.lunacore.faceengine.messageResId
import ru.visionlabs.sdk.lunacore.utils.LunaUtils
import ru.visionlabs.sdk.lunacore.utils.LunaUtils.scalePreviewDistance

class OverlayFragment : Fragment() {

    private val viewModel: OverlayViewModel by viewModels()

    private var _binding: FragmentOverlayBinding? = null
    private val binding get() = _binding!!

    private val interactionTipsHandler = Handler(Looper.getMainLooper())
    private val errorShowHandler = Handler(Looper.getMainLooper())

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

        binding.overlayViewport.isVisible = !Settings.overlayShowDetection
        binding.overlayDetection.isVisible = Settings.overlayShowDetection
        binding.overlayLegend.isVisible = Settings.overlayShowDetection
        binding.faceZone.isVisible = Settings.commandsOverridden

        LunaID.detectionCoordinates()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                processDetectRect(it.data)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        LunaID.allEvents()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                Log.d("@@@@EVENT", "event: $it")
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        LunaID.allEvents()
            .filterIsInstance<LunaID.Event.StateInteractionStarted>()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                Log.d("@@@@OVER_INTERACTION", "event: $it")
                processInteractionEvent(it.type)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        LunaID.detectionErrors()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                Log.d("@@@@OVER_ERROR", "event: $it")
                processError(it.error)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        LunaID.allEvents()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                if(it is LunaID.Event.SecurityCheck.Success){
                    Toast.makeText(this.activity, "Security validation success", Toast.LENGTH_SHORT).show()
                }else if(it is LunaID.Event.SecurityCheck.Failure){
                    Toast.makeText(this.activity, "Security validation failed", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
            }.flowOn(Dispatchers.Main)
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun processInteractionEvent(@LunaInteractionType type: Int) {
        val text = getInteractionTip(type)

        interactionTipsHandler.post {
            interactionTipsHandler.removeCallbacksAndMessages(null)
            binding.overlayInteraction.text = text
        }
        interactionTipsHandler.postDelayed(1000L) {
            binding.overlayError.text = ""
        }
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
    private fun processDetectRect(rect: RectF) {
        if (!Settings.overlayShowDetection) return

        val lc = App.lunaConfig
        val bd = LunaID.borderDistances

        val frameSize = lc.detectFrameSize
        val frameSizeRect = RectF(
            0f, 0f, frameSize.toFloat(), frameSize.toFloat(),
        ).apply {  offsetTo(rect.left, rect.top) }
        val scaledFrameSizedRect = LunaUtils.scalePreviewRect(frameSizeRect)

        val w = binding.overlayDetection.measuredWidth
        val h = binding.overlayDetection.measuredHeight

        val borderDistanceRect = if (w > 0 && h > 0) RectF(
            scalePreviewDistance(bd.fromLeft),
            scalePreviewDistance(bd.fromTop),
            w - scalePreviewDistance(bd.fromRight),
            h - scalePreviewDistance(bd.fromBottom),
        ) else null

        binding.overlayDetection.update(
            faceDetectionRect = rect,
            minFaceDetectionRect = scaledFrameSizedRect,
            borderDistanceRect = borderDistanceRect,
        )
    }

    private fun processError(error: DetectionError) {
        val errorTextResId = error.messageResId() ?: return

        errorShowHandler.post {
            errorShowHandler.removeCallbacksAndMessages(null)
            binding.overlayError.setText(errorTextResId)
            errorShowHandler.postDelayed(1000L) {
                binding.overlayError.text = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interactionTipsHandler.removeCallbacksAndMessages(null)
        errorShowHandler.removeCallbacksAndMessages(null)
        _binding = null
    }

}