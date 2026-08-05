package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register an Account
        binding.btnSignUp.setOnClickListener {
            createAccount()
        }

        binding.ibBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        // Navigation to Log in (Already have an account?)
        binding.tvLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }


    private fun createAccount() {
        binding.apply {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                Toast.makeText(requireContext(),"Verification email sent!",Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registerFragment_to_emailVerificationFragment)
                            }?.addOnFailureListener { task ->
                                Toast.makeText(requireContext(),"${task.message}",Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { task ->
                            Toast.makeText(requireContext(), "${task.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(requireContext(), "Password does not match!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}