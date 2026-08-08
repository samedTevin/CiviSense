package com.samedtevin.bagcilarapp.state

import android.util.Log


sealed class LoginState {
    object Idle : LoginState()
    object Loading: LoginState()
    object Success: LoginState()
    object AnonymousLogin: LoginState()
    data class VerifyEmail(val message: String) : LoginState()
    data class Error(val message: String): LoginState()
}