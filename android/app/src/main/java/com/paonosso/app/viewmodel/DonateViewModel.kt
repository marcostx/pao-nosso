package com.paonosso.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.CreateDonationRequest
import com.paonosso.app.data.model.DeliveryMethod
import com.paonosso.app.data.model.DonationCategory
import com.paonosso.app.data.model.Institution
import com.paonosso.app.data.model.Janela
import com.paonosso.app.data.repository.DonationRepository
import com.paonosso.app.data.repository.InstitutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DonateUiState(
    val step: Int = 1,
    val titulo: String = "",
    val descricao: String = "",
    val quantidade: String = "",
    val categoria: String = DonationCategory.PERECIVEL,
    val metodoEntrega: String = DeliveryMethod.SOLICITAR_COLETA,
    val janela: String = Janela.HOJE,
    val horario: String = "14:00",
    val instituicaoId: String? = null,
    val enderecoRetirada: String = "",
    val instituicoes: List<Institution> = emptyList(),
    val submitting: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
)

class DonateViewModel(
    private val donationRepo: DonationRepository,
    private val institutionRepo: InstitutionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DonateUiState())
    val state: StateFlow<DonateUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val list = institutionRepo.list().getOrElse { emptyList() }
            _state.value = _state.value.copy(instituicoes = list)
        }
    }

    fun next() {
        if (_state.value.step < 3) _state.value = _state.value.copy(step = _state.value.step + 1)
    }

    fun back() {
        if (_state.value.step > 1) _state.value = _state.value.copy(step = _state.value.step - 1)
    }

    fun update(transform: (DonateUiState) -> DonateUiState) {
        _state.value = transform(_state.value)
    }

    fun submit() {
        val s = _state.value
        if (s.titulo.isBlank()) {
            _state.value = s.copy(error = "Informe o titulo do item")
            return
        }
        val instituicaoId = if (s.metodoEntrega == DeliveryMethod.EU_ENTREGO) {
            s.instituicaoId ?: run {
                _state.value = s.copy(error = "Escolha um ponto de coleta")
                return
            }
        } else {
            null
        }
        val endereco = if (s.metodoEntrega == DeliveryMethod.SOLICITAR_COLETA) {
            s.enderecoRetirada.ifBlank { "Endereco a confirmar" }
        } else {
            null
        }
        _state.value = s.copy(submitting = true, error = null)
        viewModelScope.launch {
            val req = CreateDonationRequest(
                titulo = s.titulo,
                categoria = s.categoria,
                metodoEntrega = s.metodoEntrega,
                janela = s.janela,
                horario = s.horario,
                descricao = s.descricao.ifBlank { null },
                quantidade = s.quantidade.ifBlank { null },
                instituicaoId = instituicaoId,
                enderecoRetirada = endereco,
            )
            val result = donationRepo.create(req)
            _state.value = result.fold(
                onSuccess = { _state.value.copy(submitting = false, success = true) },
                onFailure = {
                    _state.value.copy(
                        submitting = false,
                        error = it.message ?: "Falha ao registrar doacao",
                    )
                },
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = PaoNossoApplication.container()
                return DonateViewModel(c.donationRepository, c.institutionRepository) as T
            }
        }
    }
}
