package com.samedtevin.bagcilarapp.state

sealed class ForgotPasswordState {
    object Idle: ForgotPasswordState()
    object Loading: ForgotPasswordState()
    data class EmailSent(val message: String): ForgotPasswordState()
    data class Error(val message: String): ForgotPasswordState()
}