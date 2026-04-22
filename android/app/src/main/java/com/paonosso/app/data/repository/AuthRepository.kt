package com.paonosso.app.data.repository

import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.local.TokenStore
import com.paonosso.app.data.model.LoginRequest
import com.paonosso.app.data.model.RegisterRequest

class AuthRepository(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) {

    suspend fun login(email: String, senha: String): Result<String> = runCatching {
        val resp = api.login(LoginRequest(email = email.trim(), senha = senha))
        tokenStore.save(
            token = resp.accessToken,
            userId = resp.userId,
            tipo = resp.tipo,
            nome = resp.nome,
            email = resp.email,
        )
        resp.tipo
    }

    suspend fun register(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        tipo: String,
    ): Result<String> = runCatching {
        val resp = api.register(
            RegisterRequest(
                nome = nome.trim(),
                email = email.trim(),
                senha = senha,
                telefone = telefone,
                tipo = tipo,
            ),
        )
        tokenStore.save(
            token = resp.accessToken,
            userId = resp.userId,
            tipo = resp.tipo,
            nome = nome,
            email = email,
        )
        resp.tipo
    }

    suspend fun logout() {
        runCatching { api.logout() }
        tokenStore.clear()
    }

    suspend fun isLoggedIn(): Boolean = !tokenStore.getToken().isNullOrEmpty()
    suspend fun currentTipo(): String? = tokenStore.getTipo()
    suspend fun currentNome(): String? = tokenStore.getNome()
    suspend fun currentEmail(): String? = tokenStore.getEmail()
}
