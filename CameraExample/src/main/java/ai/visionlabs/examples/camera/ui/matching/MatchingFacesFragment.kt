package ai.visionlabs.examples.camera.ui.matching

import ai.visionlabs.examples.camera.databinding.FragmentMatchingFacesBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MatchingFacesFragment : Fragment() {

    private val TAG = "MatchingFacesFragment"

    private val viewModel: MatchingFacesViewModel by viewModels()
    private var _binding: FragmentMatchingFacesBinding? = null
    private val binding get() = _binding!!

    private var currentBestShots: MatchingFacesState.BestShotsResult? = null

    companion object {
        fun newInstance() = MatchingFacesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingFacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is MatchingFacesState.Init -> renderInit()
                    is MatchingFacesState.InProgress -> renderProgress()
                    is MatchingFacesState.BestShotsResult -> renderBestShots(state)
                    is MatchingFacesState.MatchingResult -> renderMatchingResult(state)
                    is MatchingFacesState.Error -> renderError(state)
                }
            }
        }
    }

    private fun renderInit() = with(binding) {
        Log.d(TAG, "renderInit()")
        errorHolder.isVisible = false
        resultHolder.isVisible = false
        progressBar.isVisible = false
    }

    private fun renderProgress() = with(binding) {
        Log.d(TAG, "renderProgress()")
        errorHolder.isVisible = false
        resultHolder.isVisible = false
        progressBar.isVisible = true
    }

    private fun renderBestShots(result: MatchingFacesState.BestShotsResult) = with(binding) {
        currentBestShots = result
        Log.d(TAG, "renderBestShots(): $result")

        errorHolder.isVisible = false
        resultHolder.isVisible = false
        progressBar.isVisible = false

        matchFaces.isEnabled = result.firstImage != null && result.secondImage != null

        result.firstImage?.let(firstImage::setImageBitmap)
        result.secondImage?.let(secondImage::setImageBitmap)
    }

    private fun renderMatchingResult(result: MatchingFacesState.MatchingResult) = with(binding) {
        Log.d(TAG, "renderMatchingResult(): $result")

        errorHolder.isVisible = false
        progressBar.isVisible = false
        resultHolder.isVisible = true

        similarityScore.text = result.score.toString()
    }

    private fun renderError(error: MatchingFacesState.Error) = with(binding) {
        Log.d(TAG, "renderError(): $error")

        errorHolder.isVisible = true
        resultHolder.isVisible = false
        progressBar.isVisible = false

        errorMessage.text = error.message
    }

    private fun initListeners() = with(binding) {
        changeFirstImage.setOnClickListener {
            viewModel.findFirstFace(requireActivity())
        }

        changeSecondImage.setOnClickListener {
            viewModel.findSecondFace(requireActivity())
        }

        matchFaces.setOnClickListener {
            val result = currentBestShots
            val first = result?.firstImage
            val second = result?.secondImage
            if (first != null && second != null) {
                viewModel.matchFaces(first, second)
            } else {
                Log.w(TAG, "matchFaces() called with null images")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}