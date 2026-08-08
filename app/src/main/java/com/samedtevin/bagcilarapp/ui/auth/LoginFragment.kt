package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentLoginBinding
import com.samedtevin.bagcilarapp.state.LoginState
import com.samedtevin.bagcilarapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Login process
        binding.btnLogIn.setOnClickListener {
            logIn()
        }

        // Continue as guest
        binding.tvContinueAsGuest.setOnClickListener {
            continueAsGuest()
        }

        binding.ibBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        // Navigation to Register & Forgot Password
        // (Don't have an account?)
        binding.tvnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPassword)
        }

        collectLoginState()

    }

    private fun logIn() {
        binding.apply {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    viewModel.loginUser(email, password)
                }
                else{
                    Toast.makeText(requireContext(),"Invalid email regex.", Toast.LENGTH_SHORT).show()
                }

            }else {
                Toast.makeText(requireContext(), "All fields must be filled!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun collectLoginState(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginState.collect { state ->
                    when(state){
                        LoginState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.linearLogin.visibility = View.GONE
                        }

                        LoginState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearLogin.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),"Login successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }

                        LoginState.AnonymousLogin ->{
                            binding.progressBar.visibility = View.GONE
                            binding.linearLogin.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),"Anonymous login successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }

                        is LoginState.VerifyEmail -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearLogin.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_emailVerificationFragment)
                        }

                        is LoginState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearLogin.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }

                        LoginState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearLogin.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun continueAsGuest(){
        viewModel.loginUserInAnonymously()
    }
}