package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val resultTipo: String? = null,
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _state.value = AuthUiState(error = "Preencha email e senha")
            return
        }
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val result = repo.login(email, senha)
            _state.value = result.fold(
                onSuccess = { AuthUiState(resultTipo = it) },
                onFailure = { AuthUiState(error = it.message ?: "Falha no login") },
            )
        }
    }

    fun register(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        tipo: String,
    ) {
        if (nome.isBlank() || email.isBlank() || senha.length < 6 || telefone.isBlank()) {
            _state.value = AuthUiState(
                error = "Preencha todos os campos (senha minima de 6 caracteres)",
            )
            return
        }
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val result = repo.register(nome, email, senha, telefone, tipo)
            _state.value = result.fold(
                onSuccess = { AuthUiState(resultTipo = it) },
                onFailure = { AuthUiState(error = it.message ?: "Falha no cadastro") },
            )
        }
    }

    fun consumeError() {
        _state.value = _state.value.copy(error = null)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = PaoNossoApplication.container().authRepository
                return AuthViewModel(repo) as T
            }
        }
    }
}
