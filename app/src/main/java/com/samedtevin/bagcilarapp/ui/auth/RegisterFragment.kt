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
import com.google.firebase.auth.FirebaseAuth
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentRegisterBinding
import com.samedtevin.bagcilarapp.model.User
import com.samedtevin.bagcilarapp.state.RegisterState
import com.samedtevin.bagcilarapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        collectRegisterState()
    }


    private fun createAccount() {
        binding.apply {
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phoneNum = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val isChecked = checkBox.isChecked
            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && phoneNum.isNotEmpty()) {
                if(Regex("^05\\d{9}$").matches(phoneNum) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if (password == confirmPassword) {
                        if(isChecked){
                            val user = User(fullName, email, phoneNum)
                            viewModel.registerUser(email, password, user)
                        }
                        else{
                            Toast.makeText(requireContext(),"Please accept the terms and policy of our app!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "Password does not match!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "Check your email or phone regex.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun collectRegisterState(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.registerState.collect { state ->
                    when(state){
                        RegisterState.Loading -> {
                            binding.linearRegister.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.ivLogo.visibility = View.VISIBLE
                        }
                        RegisterState.Success -> {
                            binding.linearRegister.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.ivLogo.visibility = View.GONE
                            findNavController().navigate(R.id.action_registerFragment_to_emailVerificationFragment)
                        }
                        is RegisterState.Error -> {
                            binding.linearRegister.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.ivLogo.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        RegisterState.Idle -> {
                            binding.linearRegister.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.ivLogo.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}