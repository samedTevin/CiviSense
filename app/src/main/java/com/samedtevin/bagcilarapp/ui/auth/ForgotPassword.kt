package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentForgotPasswordBinding


class ForgotPassword : Fragment() {


    private var _binding: FragmentForgotPasswordBinding? = null
    val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
    }

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

        binding.btnSendResetLink.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword(){
        val email = binding.etEmail.text.toString()

        if(email.isEmpty()){
            Toast.makeText(requireContext(),"Fill the email field", Toast.LENGTH_SHORT).show()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(requireContext(),"Invalid Email Regex",Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Password reset email sent.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { task ->
                Toast.makeText(
                    requireContext(),
                    task.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}