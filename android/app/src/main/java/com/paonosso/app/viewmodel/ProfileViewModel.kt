package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.Stats
import com.paonosso.app.data.repository.AuthRepository
import com.paonosso.app.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val nome: String? = null,
    val email: String? = null,
    val tipo: String? = null,
    val stats: Stats? = null,
    val loading: Boolean = false,
)

class ProfileViewModel(
    private val statsRepo: StatsRepository,
    private val authRepo: AuthRepository,
    private val getNome: suspend () -> String?,
    private val getEmail: suspend () -> String?,
    private val getTipo: suspend () -> String?,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(loading = true))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val stats = statsRepo.me().getOrNull()
            _state.value = ProfileUiState(
                nome = getNome(),
                email = getEmail(),
                tipo = getTipo(),
                stats = stats,
                loading = false,
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onLoggedOut()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return ProfileViewModel(
                    c.statsRepository,
                    c.authRepository,
                    { c.tokenStore.getNome() },
                    { c.tokenStore.getEmail() },
                    { c.tokenStore.getTipo() },
                ) as T
            }
        }
    }
}
