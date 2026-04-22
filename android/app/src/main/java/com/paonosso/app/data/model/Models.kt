package com.paonosso.app.data.model

import com.google.gson.annotations.SerializedName

// Health
data class HealthResponse(
    val status: String,
    val message: String,
    val version: String,
    val timestamp: String,
)

// Auth
data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String,
    val tipo: String,
)

data class LoginRequest(val email: String, val senha: String)

data class AuthResponse(
    @SerializedName("user_id") val userId: String,
    val tipo: String,
    @SerializedName("access_token") val accessToken: String,
    val message: String?,
    val nome: String?,
    val email: String?,
)

data class User(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val tipo: String,
    @SerializedName("created_at") val createdAt: String,
)

// Donations
data class Donation(
    val id: String,
    @SerializedName("doador_id") val doadorId: String,
    @SerializedName("doador_nome") val doadorNome: String?,
    val titulo: String,
    val descricao: String?,
    val quantidade: String?,
    val categoria: String,
    val janela: String?,
    val horario: String?,
    @SerializedName("metodo_entrega") val metodoEntrega: String,
    @SerializedName("endereco_retirada") val enderecoRetirada: String?,
    val bairro: String?,
    @SerializedName("instituicao_id") val instituicaoId: String?,
    @SerializedName("instituicao_nome") val instituicaoNome: String?,
    val status: String,
)

data class CreateDonationRequest(
    val titulo: String,
    val categoria: String,
    @SerializedName("metodo_entrega") val metodoEntrega: String,
    val janela: String? = null,
    val horario: String? = null,
    val descricao: String? = null,
    val quantidade: String? = null,
    @SerializedName("instituicao_id") val instituicaoId: String? = null,
    @SerializedName("endereco_retirada") val enderecoRetirada: String? = null,
    val bairro: String? = null,
)

// Institutions
data class Institution(
    val id: String,
    @SerializedName("nome_instituicao") val nomeInstituicao: String,
    val tipo: String,
    val descricao: String?,
    @SerializedName("endereco_completo") val enderecoCompleto: String,
    val bairro: String?,
    @SerializedName("telefone_contato") val telefoneContato: String,
    val aprovado: Boolean,
)

// Appointments (achatado em /api/solicitacoes/agendamentos)
data class Appointment(
    val id: String,
    @SerializedName("doacao_id") val doacaoId: String?,
    val item: String?,
    val categoria: String?,
    val metodo: String?,
    val janela: String?,
    val horario: String?,
    val endereco: String?,
    @SerializedName("instituicao_id") val instituicaoId: String?,
    @SerializedName("instituicao_nome") val instituicaoNome: String?,
    val status: String,
)

// Solicitacoes (raw, usado pela instituicao)
data class Solicitacao(
    val id: String,
    @SerializedName("doacao_id") val doacaoId: String,
    val doacao: Donation?,
    @SerializedName("instituicao_id") val instituicaoId: String,
    val status: String,
    val observacoes: String?,
)

data class CreateSolicitacaoRequest(
    @SerializedName("doacao_id") val doacaoId: String,
    val observacoes: String? = null,
)

data class Stats(
    @SerializedName("doacoes_total") val doacoesTotal: Int,
    @SerializedName("doacoes_concluidas") val doacoesConcluidas: Int,
    @SerializedName("peso_total_kg") val pesoTotalKg: Double,
    @SerializedName("refeicoes_salvas") val refeicoesSalvas: Int,
    @SerializedName("instituicoes_ajudadas") val instituicoesAjudadas: Int? = null,
    @SerializedName("doadores_atendidos") val doadoresAtendidos: Int? = null,
)

data class MessageResponse(val message: String)
data class ErrorResponse(val error: String)

object UserType {
    const val DOADOR = "DOADOR"
    const val INSTITUICAO = "INSTITUICAO"
}

object DonationCategory {
    const val PERECIVEL = "PERECIVEL"
    const val NAO_PERECIVEL = "NAO_PERECIVEL"
    const val REFEICAO_PRONTA = "REFEICAO_PRONTA"
    const val HORTIFRUTI = "HORTIFRUTI"

    val ALL = listOf(
        PERECIVEL to "Perecivel",
        NAO_PERECIVEL to "Nao-perecivel",
        REFEICAO_PRONTA to "Refeicao Pronta",
        HORTIFRUTI to "Hortifruti",
    )
}

object DeliveryMethod {
    const val EU_ENTREGO = "EU_ENTREGO"
    const val SOLICITAR_COLETA = "SOLICITAR_COLETA"
}

object Janela {
    const val HOJE = "HOJE"
    const val AMANHA = "AMANHA"
}
