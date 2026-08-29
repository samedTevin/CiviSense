package com.samedtevin.bagcilarapp.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.samedtevin.bagcilarapp.databinding.BottomSheetPhotoBinding

class PhotoBottomSheetDialog(private val onTakePhoto: () -> Unit, private val onChooseFromGallery: () -> Unit): BottomSheetDialogFragment() {

    private var _binding: BottomSheetPhotoBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetPhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.cvTakePhoto.setOnClickListener {
            onTakePhoto()
            dismiss()
        }

        binding.cvChooseFromGallery.setOnClickListener {
            onChooseFromGallery
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}