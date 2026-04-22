package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.Donation
import com.paonosso.app.data.model.Solicitacao
import com.paonosso.app.data.repository.AppointmentRepository
import com.paonosso.app.data.repository.DonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InstitutionHomeUiState(
    val loading: Boolean = false,
    val available: List<Donation> = emptyList(),
    val sentIds: Set<String> = emptySet(),
    val error: String? = null,
)

class InstitutionHomeViewModel(
    private val donationRepo: DonationRepository,
    private val appointmentRepo: AppointmentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(InstitutionHomeUiState(loading = true))
    val state: StateFlow<InstitutionHomeUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val list = donationRepo.listAvailable().getOrElse { emptyList() }
            val sent = appointmentRepo.received().getOrElse { emptyList() }
                .map { it.doacaoId }
                .toSet()
            _state.value = InstitutionHomeUiState(loading = false, available = list, sentIds = sent)
        }
    }

    fun request(doacaoId: String) {
        viewModelScope.launch {
            appointmentRepo.create(doacaoId)
            refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return InstitutionHomeViewModel(
                    c.donationRepository,
                    c.appointmentRepository,
                ) as T
            }
        }
    }
}

data class InstitutionRequestsUiState(
    val loading: Boolean = false,
    val items: List<Solicitacao> = emptyList(),
)

class InstitutionRequestsViewModel(private val repo: AppointmentRepository) : ViewModel() {
    private val _state = MutableStateFlow(InstitutionRequestsUiState(loading = true))
    val state: StateFlow<InstitutionRequestsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val items = repo.received().getOrElse { emptyList() }
            _state.value = InstitutionRequestsUiState(loading = false, items = items)
        }
    }

    fun cancel(id: String) { viewModelScope.launch { repo.cancel(id); refresh() } }
    fun conclude(id: String) { viewModelScope.launch { repo.conclude(id); refresh() } }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return InstitutionRequestsViewModel(c.appointmentRepository) as T
            }
        }
    }
}
