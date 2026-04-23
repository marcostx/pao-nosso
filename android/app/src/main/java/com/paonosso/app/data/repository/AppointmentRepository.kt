package com.paonosso.app.data.repository

import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.model.Appointment
import com.paonosso.app.data.model.CreateSolicitacaoRequest
import com.paonosso.app.data.model.Solicitacao

class AppointmentRepository(private val api: ApiService) {

    suspend fun list(status: String? = null): Result<List<Appointment>> = runCatching {
        api.listAppointments(status)
    }

    suspend fun received(): Result<List<Solicitacao>> = runCatching { api.receivedSolicitacoes() }

    suspend fun create(doacaoId: String, observacoes: String? = null): Result<Solicitacao> =
        runCatching { api.createSolicitacao(CreateSolicitacaoRequest(doacaoId, observacoes)) }

    suspend fun accept(id: String): Result<Solicitacao> = runCatching { api.acceptSolicitacao(id) }
    suspend fun refuse(id: String): Result<Solicitacao> = runCatching { api.refuseSolicitacao(id) }
    suspend fun cancel(id: String): Result<Solicitacao> = runCatching { api.cancelSolicitacao(id) }
    suspend fun conclude(id: String): Result<Solicitacao> = runCatching {
        api.concludeSolicitacao(id)
    }

    /** Cancela uma Doacao "solta" (status AGUARDANDO na agenda do doador):
     *  exclui a doacao no backend ja que ela nao tem solicitacao associada. */
    suspend fun cancelDoacao(doacaoId: String): Result<Unit> = runCatching {
        api.deleteDonation(doacaoId)
        Unit
    }
}
