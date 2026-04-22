package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.Appointment
import com.paonosso.app.data.model.Stats
import com.paonosso.app.data.repository.AppointmentRepository
import com.paonosso.app.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = false,
    val nome: String? = null,
    val stats: Stats? = null,
    val proximas: List<Appointment> = emptyList(),
    val error: String? = null,
)

class HomeViewModel(
    private val statsRepo: StatsRepository,
    private val appointmentRepo: AppointmentRepository,
    private val nomeProvider: suspend () -> String?,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(loading = true))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val nome = nomeProvider()
            val stats = statsRepo.me().getOrNull()
            val proximas = appointmentRepo.list(status = "ACEITA").getOrElse { emptyList() }
            _state.value = HomeUiState(
                loading = false,
                nome = nome,
                stats = stats,
                proximas = proximas,
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return HomeViewModel(
                    c.statsRepository,
                    c.appointmentRepository,
                ) { c.tokenStore.getNome() } as T
            }
        }
    }
}
