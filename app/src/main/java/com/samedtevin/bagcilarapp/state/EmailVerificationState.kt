package com.samedtevin.bagcilarapp.state

sealed class EmailVerificationState {
    object Idle: EmailVerificationState()
    object Loading: EmailVerificationState()
    object Success: EmailVerificationState()
    data class ChangeEmail(val message: String): EmailVerificationState()
    data class Error(val message: String): EmailVerificationState()

}