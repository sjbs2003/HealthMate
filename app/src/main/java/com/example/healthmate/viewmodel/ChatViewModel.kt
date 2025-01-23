package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.ChatRequest
import com.example.healthmate.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(
        val messages: List<ChatMessage> = emptyList(),
        val currentInput: String = ""
    ) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Success())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            val currentState = _uiState.value as? ChatUiState.Success ?: return@launch

            // Add user message to chat history
            val updatedMessages = currentState.messages + ChatMessage(message, true)
            _uiState.value = ChatUiState.Success(messages = updatedMessages, currentInput = "")

            // Show loading state while getting response
            _uiState.value = ChatUiState.Loading

            try {
                val chatRequest = ChatRequest(message)
                val result = repository.getChatReply(chatRequest.message)

                result.fold(
                    onSuccess = { reply ->
                        val newMessages = updatedMessages + ChatMessage(reply, false)
                        _uiState.value = ChatUiState.Success(messages = newMessages)
                    },
                    onFailure = { exception ->
                        _uiState.value = ChatUiState.Error(
                            exception.message ?: "Failed to get response"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun updateInputMessage(message: String) {
        val currentState = _uiState.value as? ChatUiState.Success ?: return
        _uiState.value = currentState.copy(currentInput = message)
    }

    fun clearError() {
        if (_uiState.value is ChatUiState.Error) {
            _uiState.value = ChatUiState.Success()
        }
    }
}