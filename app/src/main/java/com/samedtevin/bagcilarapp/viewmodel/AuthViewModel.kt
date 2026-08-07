package com.samedtevin.bagcilarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samedtevin.bagcilarapp.model.User
import com.samedtevin.bagcilarapp.repository.AuthRepository
import com.samedtevin.bagcilarapp.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private var _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()

    fun registerUser(email: String, password: String,user: User){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                authRepository.register(email, password)
                val uid = authRepository.getUserUid()
                authRepository.saveUser(uid, user)
                _registerState.value = RegisterState.Success
            }
            catch (e: Exception){
                _registerState.value = RegisterState.Error(e.localizedMessage ?: "Unknown error")
            }

        }
    }
}