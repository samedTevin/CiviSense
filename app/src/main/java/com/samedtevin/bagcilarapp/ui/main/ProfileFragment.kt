package com.samedtevin.bagcilarapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentProfileBinding
import com.samedtevin.bagcilarapp.util.AlertDialog

class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_personalInformationFragment)
        }

        binding.cvApperance.setOnClickListener {
            AlertDialog.showAppearence(requireContext(),layoutInflater)
        }

        binding.cvSupport.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_helpAndSupportFragment)
        }

        binding.cvAbout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)
        }
    }



}