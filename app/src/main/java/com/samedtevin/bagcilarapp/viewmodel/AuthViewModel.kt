package com.samedtevin.bagcilarapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samedtevin.bagcilarapp.model.User
import com.samedtevin.bagcilarapp.repository.AuthRepository
import com.samedtevin.bagcilarapp.state.EmailVerificationState
import com.samedtevin.bagcilarapp.state.ForgotPasswordState
import com.samedtevin.bagcilarapp.state.LoginState
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
    private var _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()
    private var _emailState = MutableStateFlow<EmailVerificationState>(EmailVerificationState.Idle)
    val emailState = _emailState.asStateFlow()
    private var _passwordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val passwordState = _passwordState.asStateFlow()

    fun registerUser(email: String, password: String,user: User){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                authRepository.register(email, password)
                authRepository.verifyEmail()
                val uid = authRepository.getUserUid()
                authRepository.saveUser(uid, user)
                _registerState.value = RegisterState.Success
            }
            catch (e: Exception){
                _registerState.value = RegisterState.Error(e.localizedMessage ?: "Unknown error")
            }

        }
    }

    fun loginUser(email: String, password: String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try{
                authRepository.reloadUser()
                authRepository.loginUser(email, password)
                if(authRepository.isEmailVerified()){
                    _loginState.value = LoginState.Success
                }
                else{
                    authRepository.verifyEmail()
                    _loginState.value = LoginState.VerifyEmail("Please verify your email!")
                }
            }catch (e: Exception){
                    _loginState.value = LoginState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loginUserInAnonymously(){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try{
                authRepository.loginAsAnon()
                _loginState.value = LoginState.AnonymousLogin
            }
            catch (e: Exception){
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun sendVerificationEmail(){
        viewModelScope.launch {
            _emailState.value = EmailVerificationState.Loading
            try{
                authRepository.verifyEmail()
                _emailState.value = EmailVerificationState.Success
            }catch (e: Exception){
                _emailState.value = EmailVerificationState.Error(e.localizedMessage ?: "Unkown error")
            }
        }
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            _passwordState.value = ForgotPasswordState.Loading
            try{
                authRepository.resetPassword(email)
                _passwordState.value = ForgotPasswordState.EmailSent("Reset password email sent.")
            }
            catch (e: Exception){
                _passwordState.value = ForgotPasswordState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun changeEmail(){
        viewModelScope.launch {
            _emailState.value = EmailVerificationState.Loading
            try{
                authRepository.changeEmail()
                _emailState.value = EmailVerificationState.ChangeEmail("Account has been deleted. Register with your new email address.")
            }catch (e: Exception){
                _emailState.value = EmailVerificationState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}