package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.BuildConfig
import ai.visionlabs.examples.camera.databinding.FragmentMainBinding
import ai.visionlabs.examples.camera.ui.settings.SettingsFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.utils.LunaUtils
import ru.visionlabs.sdk.lunacore.utils.LunaUtils.V52
import ru.visionlabs.sdk.lunacore.utils.LunaUtils.V59
import ru.visionlabs.sdk.R


class MainFragment : Fragment() {

    val TAG = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.stateLiveData.observe(this) {
            updateUi(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(this.viewLifecycleOwner)

        LunaID.engineInitStatus
            .flowWithLifecycle(this.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                if(it is LunaID.EngineInitStatus.InProgress) {
                    binding.showCameraWithDetection.isEnabled = false
                    binding.showCameraWithFrame.isEnabled = false
                    binding.showCameraAndRecordVideo.isEnabled = false
                    binding.showCameraWithInteraction.isEnabled = false
                    binding.showCameraWithCommands.isEnabled = false
                }else if(it is LunaID.EngineInitStatus.Success) {
                    binding.showCameraWithDetection.isEnabled = true
                    binding.showCameraWithFrame.isEnabled = true
                    binding.showCameraAndRecordVideo.isEnabled = true
                    binding.showCameraWithInteraction.isEnabled = true
                    binding.showCameraWithCommands.isEnabled = true
                }
            }.flowOn(Dispatchers.Main)
            .launchIn(this.lifecycleScope)
    }

    private fun updateUi(s: MainViewState) {
        Log.d(TAG, "updateUi() called with: s = $s")
        when (s) {
            is MainViewState.Init -> {
                binding.sdkVersion.text = BuildConfig.SDK_VERSION
            }
            is MainViewState.Image -> {
//                val dv52 = LunaUtils.getDescriptor(s.image.image, V52)
//                val dv59 = LunaUtils.getDescriptor(s.image.image, V59)
                renderImage(s)
                renderVideoPath(s.videoPath)
            }
            is MainViewState.Error -> {
                renderError(s)
            }
            is MainViewState.Cancelled -> {
                renderVideoPath(s.videoPath)
            }
        }
    }

    private fun renderVideoPath(videoPath: String?) {
        Log.d(TAG, "renderVideoPath() called with: videoPath = $videoPath")

        val t = "Video session path:\n$videoPath"
        binding.videoFilePath.text = t
    }

    private fun renderError(s: MainViewState.Error) {
        Log.d("@@@@@", "error: ${s.message}")
    }

    private fun renderImage(s: MainViewState.Image) {
        binding.bestShotImage.setImageBitmap(s.image.warp)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        with(binding) {
            showCameraWithDetection.setOnClickListener {
                viewModel.onShowCameraWithDetectionClicked(requireActivity())
            }
            showCameraWithFrame.setOnClickListener {
                viewModel.onShowCameraWithFrameClicked(requireActivity())
            }
            showCameraAndRecordVideo.setOnClickListener {
                viewModel.onShowCameraAndRecordVideo(requireActivity())

            }
            showCameraWithInteraction.setOnClickListener {
                viewModel.onShowCameraWithInteraction(requireActivity())
            }
            showCameraWithCommands.setOnClickListener {
                viewModel.onShowCameraWithCommands(
                    requireActivity(),
                    binding.overrideStart.isChecked,
                    binding.overrideClose.isChecked
                )
            }
            darkTheme.setOnCheckedChangeListener { _, isChecked ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
            settingsButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}