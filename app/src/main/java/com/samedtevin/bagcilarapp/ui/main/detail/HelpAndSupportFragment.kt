package com.samedtevin.bagcilarapp.ui.main.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentHelpAndSupportBinding
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController


class HelpAndSupportFragment : Fragment() {

    private var _binding: FragmentHelpAndSupportBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpAndSupportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvAskAi.setOnClickListener {
            findNavController().navigate(R.id.action_helpAndSupportFragment_to_smartAssistantFragment)
        }

        binding.btnContact.setOnClickListener {
            val phoneUri = "tel:02124100600".toUri()
            val intent = Intent(Intent.ACTION_DIAL,phoneUri)
            startActivity(intent)
        }

        binding.faqReportHeader.setOnClickListener {
            if(binding.tvFaqReportAnswer.visibility == View.GONE){
                binding.tvFaqReportAnswer.visibility = View.VISIBLE
                binding.ivFaqReportArrow.rotation = 90f
            }
            else{
                binding.tvFaqReportAnswer.visibility = View.GONE
                binding.ivFaqReportArrow.rotation = 0f
            }
        }

        binding.faqLocationHeader.setOnClickListener {
            if(binding.tvFaqLocationAnswer.visibility == View.GONE){
                binding.tvFaqLocationAnswer.visibility = View.VISIBLE
                binding.ivFaqLocationArrow.rotation = 90f
            }else{
                binding.tvFaqLocationAnswer.visibility = View.GONE
                binding.ivFaqLocationArrow.rotation = 0f
            }
        }

        binding.faqPhotoHeader.setOnClickListener {
            if(binding.tvFaqPhotoAnswer.visibility == View.GONE){
                binding.tvFaqPhotoAnswer.visibility = View.VISIBLE
                binding.ivFaqPhotoArrow.rotation = 90f
            }
            else{
                binding.tvFaqPhotoAnswer.visibility = View.GONE
                binding.ivFaqPhotoArrow.rotation = 0f
            }
        }
    }
}