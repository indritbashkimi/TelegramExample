package com.ibashkimi.telegram.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibashkimi.telegram.data.Authentication
import com.ibashkimi.telegram.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: TelegramClient
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)

    val uiState: State<UiState> get() = _uiState

    init {
        client.authState.onEach {
            when (it) {
                Authentication.UNAUTHENTICATED, Authentication.UNKNOWN -> {
                    _uiState.value = UiState.Loading
                }
                Authentication.WAIT_FOR_NUMBER -> {
                    _uiState.value = UiState.InsertNumber()
                }
                Authentication.WAIT_FOR_CODE -> {
                    _uiState.value = UiState.InsertCode()
                }
                Authentication.WAIT_FOR_PASSWORD -> {
                    _uiState.value = UiState.InsertPassword()
                }
                Authentication.AUTHENTICATED -> {
                    _uiState.value = UiState.Authenticated
                }
            }
        }.launchIn(viewModelScope)
    }

    fun insertPhoneNumber(number: String) {
        _uiState.value = UiState.Loading
        client.insertPhoneNumber(number)
    }

    fun insertCode(code: String) {
        _uiState.value = UiState.Loading
        client.insertCode(code)
    }

    fun insertPassword(password: String) {
        _uiState.value = UiState.Loading
        client.insertPassword(password)
    }
}

sealed class UiState {
    object Loading : UiState()
    data class InsertNumber(val previousError: Throwable? = null) : UiState()
    data class InsertCode(val previousError: Throwable? = null) : UiState()
    data class InsertPassword(val previousError: Throwable? = null) : UiState()
    object Authenticated : UiState()
}