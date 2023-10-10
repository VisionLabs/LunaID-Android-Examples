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
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.LunaInteractionType
import ru.visionlabs.sdk.lunacore.faceengine.DetectionError
import ru.visionlabs.sdk.lunacore.faceengine.messageResId

class OverlayFragment : Fragment() {

    private val viewModel: OverlayViewModel by viewModels()

    private var _binding: FragmentOverlayBinding? = null
    private val binding get() = _binding!!

    private val interactionTipsHandler = Handler(Looper.getMainLooper())

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

        LunaID.detectionCoordinates()
            .flowOn(Dispatchers.IO)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                processDetectRect(it.data)
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

    }

    private fun processInteractionEvent(@LunaInteractionType type: Int) {
        val text = getInteractionTip(type)

        interactionTipsHandler.removeCallbacksAndMessages(null)

        binding.overlayInteraction.text = text

        interactionTipsHandler.removeCallbacksAndMessages(null)
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

    private fun processDetectRect(rect: RectF) {
        if (!Settings.overlayShowDetection) return
        Log.d("@@@@", "@@@@")

        val frameSize = App.lunaConfig.detectFrameSize
        val frameSizeRect = RectF(
            0f, 0f, frameSize.toFloat(), frameSize.toFloat(),
        ).apply {  offsetTo(rect.left, rect.top) }

        val borderDistancePx = App.lunaConfig.borderDistance
        binding.overlayDetection.update(
            faceDetectionRect = rect,
            minFaceDetectionRect = frameSizeRect,
            borderDistancePx = borderDistancePx,
        )
    }

    private fun processError(error: DetectionError) {
        val errorTextResId = error.messageResId() ?: return
        binding.overlayError.setText(errorTextResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}