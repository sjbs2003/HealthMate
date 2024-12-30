package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// State classes
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val userToken: String? = null,
    val phoneNumber: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    // UI States
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    //Handle SignUp
    fun signUp(name: String, phone: String, email: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.signup(name, phone, email)
                result.fold(
                    onSuccess = { token ->
                        _authState.value = AuthState.Success(token)
                        // update ui state
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = true,
                            userToken = token
                        )
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(exception.message ?: "SignUp Failed")
                    }
                )
            } catch (e:Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Handle Login
    fun login(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.login(phone)
                result.fold(
                    onSuccess = {
                        _authState.value = AuthState.OtpSent
                        // store phone no for verification
                        _uiState.value = _uiState.value.copy(
                            phoneNumber = phone
                        )
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(exception.message ?: "Login Error")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown Error Occurred")
            }
        }
    }

    // Handle OTP Verification
    fun verifyOTP(otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val phone = _uiState.value.phoneNumber
                if (phone.isNullOrEmpty()) {
                    _authState.value = AuthState.Error("Phone number not found!!")
                    return@launch
                }

                val result = repository.verifyOTP(phone, otp)
                result.fold(
                    onSuccess = { token ->
                        _authState.value = AuthState.Success(token)
                        // update ui state
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = true,
                            userToken = token
                        )
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(exception.message ?: "OTP Verification Failed")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown Error Occurred")
            }
        }
    }

    // Reset Auth State
    fun resetAuthState() { _authState.value = AuthState.Initial }

    // Reset Error
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Initial
        }
    }

    // Logout
    fun logout() {
        _uiState.value = AuthUiState()
        _authState.value = AuthState.Initial
    }
}