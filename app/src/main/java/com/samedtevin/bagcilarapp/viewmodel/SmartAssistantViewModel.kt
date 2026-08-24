package com.samedtevin.bagcilarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samedtevin.bagcilarapp.data.local.ChatDataStore
import com.samedtevin.bagcilarapp.model.ChatMessage
import com.samedtevin.bagcilarapp.repository.SmartAssistantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartAssistantViewModel @Inject constructor(private val aiRepository: SmartAssistantRepository, private val chatDataStore: ChatDataStore): ViewModel() {

    private val _response = MutableStateFlow<String?>(null)
    val response = _response.asStateFlow()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var pendingQuestion: String? = null

    init {
        loadChat()
    }

    private fun loadChat() {
        viewModelScope.launch {

            val savedMessages = chatDataStore.messages.first()
            val pending = chatDataStore.getPendingQuestion()

            pendingQuestion = pending

            _messages.value = if (!pending.isNullOrBlank()) {
                savedMessages + ChatMessage(
                    message = pending,
                    isUser = true
                )
            } else {
                savedMessages
            }

            if (!pending.isNullOrBlank()) {
                _error.value =
                    "We couldn't answer your previous question. Please try again."
            } else {
                _error.value = null
            }
        }
    }

    fun askAi(question: String) {

        if (question.isBlank()) return
        if (_isLoading.value) return

        viewModelScope.launch {

            _error.value = null
            _isLoading.value = true

            pendingQuestion = question

            val conversation = _messages.value + ChatMessage(
                message = question,
                isUser = true
            )

            _messages.value = conversation

            chatDataStore.savePendingQuestion(question)

            requestAi(conversation)
        }
    }

    fun retry() {

        if (_isLoading.value) return

        viewModelScope.launch {

            val question = chatDataStore.getPendingQuestion()
                ?: return@launch

            pendingQuestion = question

            _error.value = null
            _isLoading.value = true

            val savedMessages = chatDataStore.messages.first()

            val conversation = savedMessages + ChatMessage(
                message = question,
                isUser = true
            )

            _messages.value = conversation

            requestAi(conversation)
        }
    }

    private suspend fun requestAi(conversation: List<ChatMessage>) {

        try {
            val aiResponse = aiRepository.askAi(conversation)

            val aiMessage = ChatMessage(
                message = aiResponse,
                isUser = false
            )

            val finalMessage = conversation + aiMessage

            _messages.value = finalMessage
            _response.value = aiResponse

            chatDataStore.saveMessage(finalMessage)
            chatDataStore.clearPendingQuestion()

            pendingQuestion = null
            _error.value = null

        } catch (e: Exception) {

            e.printStackTrace()

            _error.value = when (e) {

                is java.net.UnknownHostException,
                is java.net.ConnectException -> {
                    "No internet connection. Please check your connection and try again."
                }

                else -> {
                    "The AI assistant is temporarily unavailable. Please try again later."
                }
            }
        }finally {
            _isLoading.value = false
        }
    }

    fun clearMessage(){
        viewModelScope.launch {
            chatDataStore.clearChat()

            _messages.value = emptyList()
            _response.value = null
            _error.value = null
            pendingQuestion = null
        }
    }
}