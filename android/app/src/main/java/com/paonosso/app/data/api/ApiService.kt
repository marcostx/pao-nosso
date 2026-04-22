package com.paonosso.app.data.api

import com.paonosso.app.data.model.Appointment
import com.paonosso.app.data.model.AuthResponse
import com.paonosso.app.data.model.CreateDonationRequest
import com.paonosso.app.data.model.CreateSolicitacaoRequest
import com.paonosso.app.data.model.Donation
import com.paonosso.app.data.model.HealthResponse
import com.paonosso.app.data.model.Institution
import com.paonosso.app.data.model.LoginRequest
import com.paonosso.app.data.model.MessageResponse
import com.paonosso.app.data.model.RegisterRequest
import com.paonosso.app.data.model.Solicitacao
import com.paonosso.app.data.model.Stats
import com.paonosso.app.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/health")
    suspend fun healthCheck(): HealthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("/api/auth/me")
    suspend fun getCurrentUser(): User

    @POST("/api/auth/logout")
    suspend fun logout(): MessageResponse

    @POST("/api/doacoes")
    suspend fun createDonation(@Body request: CreateDonationRequest): Donation

    @GET("/api/doacoes/disponiveis")
    suspend fun listAvailableDonations(
        @Query("bairro") bairro: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("janela") janela: String? = null,
    ): List<Donation>

    @GET("/api/doacoes/minhas")
    suspend fun listMyDonations(): List<Donation>

    @GET("/api/doacoes/{id}")
    suspend fun donationDetail(@Path("id") id: String): Donation

    @DELETE("/api/doacoes/{id}")
    suspend fun deleteDonation(@Path("id") id: String): MessageResponse

    @POST("/api/instituicoes")
    suspend fun createInstitution(@Body body: Map<String, String?>): Institution

    @GET("/api/instituicoes")
    suspend fun listInstitutions(@Query("bairro") bairro: String? = null): List<Institution>

    @GET("/api/instituicoes/me")
    suspend fun myInstitution(): Institution

    @POST("/api/solicitacoes")
    suspend fun createSolicitacao(@Body request: CreateSolicitacaoRequest): Solicitacao

    @GET("/api/solicitacoes/recebidas")
    suspend fun receivedSolicitacoes(): List<Solicitacao>

    @GET("/api/solicitacoes/agendamentos")
    suspend fun listAppointments(@Query("status") status: String? = null): List<Appointment>

    @PUT("/api/solicitacoes/{id}/aceitar")
    suspend fun acceptSolicitacao(@Path("id") id: String): Solicitacao

    @PUT("/api/solicitacoes/{id}/recusar")
    suspend fun refuseSolicitacao(@Path("id") id: String): Solicitacao

    @PUT("/api/solicitacoes/{id}/cancelar")
    suspend fun cancelSolicitacao(@Path("id") id: String): Solicitacao

    @PUT("/api/solicitacoes/{id}/concluir")
    suspend fun concludeSolicitacao(@Path("id") id: String): Solicitacao

    @GET("/api/stats/me")
    suspend fun stats(): Stats
}
