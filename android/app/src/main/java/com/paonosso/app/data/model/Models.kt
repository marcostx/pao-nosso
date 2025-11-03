package com.paonosso.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelos de dados da aplicação
 */

// Health Check
data class HealthResponse(
    val status: String,
    val message: String,
    val version: String,
    val timestamp: String
)

// Authentication
data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String,
    val tipo: String // "DOADOR" ou "INSTITUICAO"
)

data class LoginRequest(
    val email: String,
    val senha: String
)

data class AuthResponse(
    @SerializedName("user_id")
    val userId: String,
    val tipo: String,
    @SerializedName("access_token")
    val accessToken: String,
    val message: String,
    val nome: String? = null,
    val email: String? = null
)

// User
data class User(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val tipo: String,
    @SerializedName("created_at")
    val createdAt: String
)

// Generic
data class MessageResponse(
    val message: String
)

data class ErrorResponse(
    val error: String
)

// User Types
enum class UserType {
    DOADOR,
    INSTITUICAO
}

