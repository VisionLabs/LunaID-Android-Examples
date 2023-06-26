package ai.visionlabs.examples.camera.ui.overlay

import ai.visionlabs.examples.camera.databinding.FragmentOverlayBinding
import ai.visionlabs.examples.camera.ui.Settings
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.visionlabs.sdk.R
import ru.visionlabs.sdk.lunacamera.presentation.camera.messageResId
import ru.visionlabs.sdk.lunacore.LunaError
import ru.visionlabs.sdk.lunacore.LunaInteraction

class OverlayFragment : Fragment() {

    private val viewModel: OverlayViewModel by lazy {
        ViewModelProvider(this, OverlayViewModelFactory()).get(OverlayViewModel::class.java)
    }

    private var _binding: FragmentOverlayBinding? = null
    private val binding get() = _binding!!

    private val interactionShowHandler = Handler(Looper.getMainLooper())
    private val MESSAGE_DELAY = 500L

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

        viewModel.overlayDelegate.receive.observe(viewLifecycleOwner) {
            processEvent(it)
        }
    }

    private fun processEvent(event: Map<String, Any>?) {
        event ?: return
        val action = event.getOrDefault("action", null) ?: return

        when (action) {
            "error" -> processError(event)
            "detect" -> processDetectRect(event)
            "interaction" -> processInteraction(event)
        }
    }

    private fun processInteraction(event: Map<String, Any>) {
        val interactionState = event["state"] as LunaInteraction

        val textId = interactionState.messageResId()
        val text: String = if (textId == null) {
            ""
        } else {
            getString(textId)
        }

        binding.overlayInteraction.text = text

        interactionShowHandler.removeCallbacksAndMessages(null)
        interactionShowHandler.postDelayed(MESSAGE_DELAY) {
            binding.overlayInteraction.text = ""
        }
    }

    private fun processDetectRect(event: Map<String, Any>) {
        if (!Settings.overlayShowDetection) return

        val rect = event["detect_rect"] as RectF
        val isError = event["is_error"] as Boolean

        binding.overlayDetection.updateFaceRect(rect, isError)
    }

    private fun processError(event: Map<String, Any>) {
        val error = event["error"]
        if (error is LunaError) {
            val errorTextResId = error.messageResId() ?: return
            binding.overlayError.setText(errorTextResId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}