package com.samedtevin.bagcilarapp.ui.main

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentReportBinding
import com.samedtevin.bagcilarapp.util.PhotoBottomSheetDialog
import com.samedtevin.bagcilarapp.viewmodel.RepositoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    val binding get() = _binding!!
    private val viewModel: RepositoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPhoto.setOnClickListener {
            PhotoBottomSheetDialog(
                onTakePhoto = {

                },
                onChooseFromGallery = {

                }
            ).show(parentFragmentManager,"addPhoto")
        }

        binding.etDescription.doAfterTextChanged {
            viewModel.updateDescription(it?.toString().orEmpty())
        }

        binding.addLocation.setOnClickListener {
            findNavController().navigate(R.id.action_reportFragment_to_locationFragment)
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_reportFragment_to_reportReviewFragment)
        }

        binding.btnClearDescription.setOnClickListener {
            binding.etDescription.text.clear()
        }

        collectDescription()
    }

    private fun collectDescription(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.description.collect { text ->
                    binding.btnClearDescription.isVisible = text.isNotEmpty()
                    if (binding.etDescription.text.toString() != text) {
                        binding.etDescription.setText(text)
                        binding.etDescription.setSelection(text.length)
                    }
                    binding.tvWordCount.text = "${text.length}/500"
                }
            }
        }
    }




}