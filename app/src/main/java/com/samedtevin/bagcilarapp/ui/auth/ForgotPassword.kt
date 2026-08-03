package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentForgotPasswordBinding


class ForgotPassword : Fragment() {


    private var _binding: FragmentForgotPasswordBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Navigation to the back page & log in 
        binding.ibBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackToLogIn.setOnClickListener{
            findNavController().navigate(R.id.action_forgotPassword_to_loginFragment)
        }
    }
}