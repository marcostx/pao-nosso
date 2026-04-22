package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.Appointment
import com.paonosso.app.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AgendaUiState(
    val loading: Boolean = false,
    val items: List<Appointment> = emptyList(),
    val error: String? = null,
)

class AgendaViewModel(private val repo: AppointmentRepository) : ViewModel() {
    private val _state = MutableStateFlow(AgendaUiState(loading = true))
    val state: StateFlow<AgendaUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val items = repo.list().getOrElse { emptyList() }
            _state.value = AgendaUiState(loading = false, items = items)
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch {
            repo.cancel(id)
            refresh()
        }
    }

    fun conclude(id: String) {
        viewModelScope.launch {
            repo.conclude(id)
            refresh()
        }
    }

    fun accept(id: String) {
        viewModelScope.launch {
            repo.accept(id)
            refresh()
        }
    }

    fun refuse(id: String) {
        viewModelScope.launch {
            repo.refuse(id)
            refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return AgendaViewModel(c.appointmentRepository) as T
            }
        }
    }
}
