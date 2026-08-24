package com.samedtevin.bagcilarapp.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val message: String = "",
    val isUser: Boolean = false
)
