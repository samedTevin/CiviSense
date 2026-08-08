package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentForgotPasswordBinding
import com.samedtevin.bagcilarapp.state.ForgotPasswordState
import com.samedtevin.bagcilarapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPassword : Fragment() {


    private var _binding: FragmentForgotPasswordBinding? = null
    val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()


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

        collectPasswordState()
    }

    private fun forgotPassword(){
        val email = binding.etEmail.text.toString()

        if(email.isEmpty()){
            Toast.makeText(requireContext(),"Fill the email field", Toast.LENGTH_SHORT).show()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(requireContext(),"Invalid Email Regex!",Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.resetPassword(email)
    }

    private fun collectPasswordState(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.passwordState.collect { state ->
                    when(state){
                        ForgotPasswordState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.linearForgotPassword.visibility = View.GONE
                        }
                        is ForgotPasswordState.EmailSent -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearForgotPassword.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        is ForgotPasswordState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearForgotPassword.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        ForgotPasswordState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearForgotPassword.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}