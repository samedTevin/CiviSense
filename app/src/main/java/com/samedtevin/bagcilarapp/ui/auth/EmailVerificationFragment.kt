package com.samedtevin.bagcilarapp.ui.auth

import android.os.Bundle
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
import com.samedtevin.bagcilarapp.databinding.FragmentEmailVerificationBinding
import com.samedtevin.bagcilarapp.state.EmailVerificationState
import com.samedtevin.bagcilarapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerificationFragment : Fragment() {

    private var _binding: FragmentEmailVerificationBinding? = null
    val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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

        collectEmailState()
    }

    private fun changeEmail(){
        viewModel.changeEmail()
    }

    private fun resendEmail(){
        viewModel.sendVerificationEmail()
    }

    private fun collectEmailState(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.emailState.collect { state ->
                    when(state){
                        EmailVerificationState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.linearEmailVerification.visibility = View.GONE
                        }
                        EmailVerificationState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearEmailVerification.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), "Verification Email Sent Again.", Toast.LENGTH_SHORT).show()
                        }
                        is EmailVerificationState.ChangeEmail -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearEmailVerification.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_emailVerificationFragment_to_registerFragment)
                        }
                        is EmailVerificationState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearEmailVerification.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                        }
                        EmailVerificationState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.linearEmailVerification.visibility = View.VISIBLE
                        }
                    }
                }

            }
        }
    }

}