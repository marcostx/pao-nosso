package com.paonosso.app.data.api

import com.paonosso.app.data.model.*
import retrofit2.http.*

/**
 * Interface que define os endpoints da API
 */
interface ApiService {
    
    // Health Check
    @GET("/health")
    suspend fun healthCheck(): HealthResponse
    
    // Authentication
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    
    @GET("/api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): User
    
    @POST("/api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): MessageResponse
}

