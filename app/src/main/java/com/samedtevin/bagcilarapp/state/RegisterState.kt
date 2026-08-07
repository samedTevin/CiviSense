package com.samedtevin.bagcilarapp.state

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading: RegisterState()
    object Success: RegisterState()
    data class Error(val message: String): RegisterState()
}