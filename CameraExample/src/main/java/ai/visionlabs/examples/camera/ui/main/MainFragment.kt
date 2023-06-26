package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.databinding.FragmentMainBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class MainFragment : Fragment() {

    val TAG = "@@@"

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

    private fun updateUi(s: MainViewState) {
        Log.d(TAG, "updateUi() called with: s = $s")
        when (s) {
            is MainViewState.Init -> {} // noop
            is MainViewState.Image -> {
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
            darkTheme.setOnCheckedChangeListener { _, isChecked ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}