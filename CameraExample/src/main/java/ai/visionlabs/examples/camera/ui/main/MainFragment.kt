package ai.visionlabs.examples.camera.ui.main

import ai.visionlabs.examples.camera.BuildConfig
import ai.visionlabs.examples.camera.databinding.FragmentMainBinding
import ai.visionlabs.examples.camera.ui.Settings
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.utils.VideoUtils
import java.io.File
import java.io.FileOutputStream


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
                }else if(it is LunaID.EngineInitStatus.Success) {
                    binding.showCameraWithDetection.isEnabled = true
                    binding.showCameraWithFrame.isEnabled = true
                    binding.showCameraAndRecordVideo.isEnabled = true
                    binding.showCameraWithInteraction.isEnabled = true
                }
            }.flowOn(Dispatchers.Main)
            .launchIn(this.lifecycleScope)

        LunaID.bestShot
            .filterNotNull()
            .onEach {
                Log.d(TAG, "onViewCreated() collected bestshot, draw it")
                binding.bestShotImage.setImageBitmap(it.bestShot.warp)
                saveBestShotFound(requireContext(),it)
            }.flowOn(Dispatchers.Main)
            .launchIn(this.lifecycleScope)
    }
    fun saveBestShotFound(
        context: Context,
        bestShotFound: LunaID.Event.BestShotFound
    ) {
        try {
            val outputDir = File(context.filesDir, "best_shots").apply { if (!exists()) mkdirs() }

            // Save BestShot image
            val bestShotFile = File(outputDir, "best_shot.jpg")
            bestShotFound.bestShot.image.compress(
                Bitmap.CompressFormat.JPEG,
                95,
                FileOutputStream(bestShotFile)
            )

            // Save BestShot warp image
            val warpFile = File(outputDir, "warp_image.jpg")
            bestShotFound.bestShot.warp.compress(Bitmap.CompressFormat.JPEG, 95, FileOutputStream(warpFile))

            // Save interaction frames
            bestShotFound.interactionFrames?.forEachIndexed { index, frame ->
                val interactionFrameFile = File(outputDir, "interaction_frame_${index}.jpg")
                frame.image.compress(Bitmap.CompressFormat.JPEG, 90, FileOutputStream(interactionFrameFile))
            }

            // Handle video if available
            bestShotFound.videoPath?.let { path ->
                Log.d("SaveBestShot", "Video path: $path")

                val videoInputUri = Uri.fromFile(File(path))
                val originalVideoFile = File(outputDir, "original_video.mp4")
                val compressedVideoFile = File(outputDir, "compressed_video.mp4")

                // Copy original video
                context.contentResolver.openInputStream(videoInputUri)?.use { input ->
                    FileOutputStream(originalVideoFile).use { output ->
                        input.copyTo(output)
                    }
                    Log.d("SaveBestShot", "Original video copied to: ${originalVideoFile.absolutePath}")
                } ?: Log.e("SaveBestShot", "Failed to open input stream for original video")

                // Compress video
                VideoUtils.compressVideo(
                    context,
                    videoInputUri,
                    compressedVideoFile,
                    onSuccess = { Log.d("SaveBestShot", "Video compressed successfully: ${compressedVideoFile.absolutePath}") },
                    onFailure = { throwable ->
                        Log.e("SaveBestShot", "Video compression failed", throwable)
                    }
                )
            } ?: Log.w("SaveBestShot", "No video path provided")

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        Log.d(TAG, "error: ${s.message}")
    }

    private fun renderImage(s: MainViewState.Image) {
        binding.bestShotImage.setImageBitmap(s.image.warp)
    }


    @SuppressLint("ClickableViewAccessibility")
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
//            showCameraAndRecordVideo.setOnClickListener {
//                viewModel.onShowCameraAndRecordVideo(requireActivity())
//
//            }
            showCameraWithInteraction.setOnClickListener {
                viewModel.onShowCameraWithInteraction(requireActivity())
            }

            detectionIsVisible.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    return@setOnTouchListener true
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    binding.detectionIsVisible.isChecked = !binding.detectionIsVisible.isChecked
                    Settings.overlayShowDetection = binding.detectionIsVisible.isChecked
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}