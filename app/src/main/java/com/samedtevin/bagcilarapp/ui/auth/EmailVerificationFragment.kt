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
import com.samedtevin.bagcilarapp.databinding.FragmentEmailVerificationBinding


class EmailVerificationFragment : Fragment() {

    private var _binding: FragmentEmailVerificationBinding? = null
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
        // Inflate the layout for this fragment
        _binding = FragmentEmailVerificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnImVerified.setOnClickListener {
            findNavController().navigate(R.id.action_emailVerificationFragment_to_loginFragment)
        }

        binding.btnResendEmail.setOnClickListener {
            resendEmail()
        }

        binding.tvChangeEmail.setOnClickListener {
            changeEmail()
        }

        binding.ibBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun changeEmail(){
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.delete().addOnSuccessListener {
                firebaseAuth.signOut()
                findNavController().navigate(R.id.action_emailVerificationFragment_to_loginFragment)
            }.addOnFailureListener { task ->
                Toast.makeText(requireContext(), "${task.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User session not found. Please log in.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_emailVerificationFragment_to_loginFragment)
        }
    }

    private fun resendEmail(){
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.sendEmailVerification().addOnSuccessListener {
                Toast.makeText(requireContext(),"Verification email sent again.",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { it ->
                Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User session not found. Please log in.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_emailVerificationFragment_to_loginFragment)
        }
    }

}