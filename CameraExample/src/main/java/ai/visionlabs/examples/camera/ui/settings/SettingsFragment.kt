package ai.visionlabs.examples.camera.ui.settings

import ai.visionlabs.examples.camera.App.Companion.lunaConfig
import ai.visionlabs.examples.camera.databinding.FragmentSettingsBinding
import ai.visionlabs.examples.camera.ui.Settings
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.visionlabs.sdk.lunacore.LunaID

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Settings.overlayShowDetection = true
                Settings.commandsOverridden = false

                context?.contentResolver?.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readAllBytes()
                    LunaID.pushIntoEngine(bytes, 1440, 1920, lunaConfig.detectFrameSize)
                }

            }
        }

        binding.settingsButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }
}

