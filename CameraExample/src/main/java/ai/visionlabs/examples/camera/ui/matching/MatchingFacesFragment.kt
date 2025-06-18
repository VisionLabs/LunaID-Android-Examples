package ai.visionlabs.examples.camera.ui.matching

import ai.visionlabs.examples.camera.databinding.FragmentMatchingFacesBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MatchingFacesFragment : Fragment() {


    private val TAG = "MatchingFacesFragment"

    private lateinit var viewModel: MatchingFacesViewModel

    companion object {
        fun newInstance() = MatchingFacesFragment()
    }

    private var _binding: FragmentMatchingFacesBinding? = null
    private val binding: FragmentMatchingFacesBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MatchingFacesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingFacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserving()
    }

    private fun setObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is MatchingFacesState.Error -> onError(it)
                    is MatchingFacesState.MatchingResult -> onMatchingResult(it)
                    is MatchingFacesState.BestShotsResult -> onBestShotsResult(it)
                    MatchingFacesState.Init -> onInit()
                    MatchingFacesState.InProgress -> onProgress()
                }
            }
        }
    }

    private fun onInit() {
        Log.d(TAG, "onInit()")
        binding.apply {
            errorHolder.visibility = View.GONE
            resultHolder.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun onProgress() {
        Log.d(TAG, "onProgress()")
        binding.apply {
            errorHolder.visibility = View.GONE
            resultHolder.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun onBestShotsResult(result: MatchingFacesState.BestShotsResult) {
        Log.d(TAG, "onBestShotsResult(): $result")
        binding.apply {
            resultHolder.visibility = View.GONE
            errorHolder.visibility = View.GONE
            matchFaces.isEnabled = result.firstImage != null && result.secondImage != null
            result.firstImage?.let {
                firstImage.setImageBitmap(it)
            }
            result.secondImage?.let {
                secondImage.setImageBitmap(it)
            }
        }
    }

    private fun onMatchingResult(result: MatchingFacesState.MatchingResult) {
        Log.d(TAG, "onMatchingResult(): $result")
        binding.apply {
            errorHolder.visibility = View.GONE
            progressBar.visibility = View.GONE
            resultHolder.visibility = View.VISIBLE
            similarityScore.text = result.score.toString()
        }
    }

    private fun onError(error: MatchingFacesState.Error) {
        Log.d(TAG, "onError(): $error")
        binding.apply {
            errorHolder.visibility = View.VISIBLE
            resultHolder.visibility = View.GONE
            progressBar.visibility = View.GONE
            errorMessage.text = error.message
        }
    }

    private fun setListeners() {
        binding.apply {
            changeFirstImage.setOnClickListener {
                viewModel.findFirstFace(requireActivity())
            }
            changeSecondImage.setOnClickListener {
                viewModel.findSecondFace(requireActivity())
            }
            matchFaces.setOnClickListener {
                viewModel.matchFaces(
                    binding.firstImage.drawToBitmap(),
                    binding.secondImage.drawToBitmap()
                )
            }
        }
    }
}