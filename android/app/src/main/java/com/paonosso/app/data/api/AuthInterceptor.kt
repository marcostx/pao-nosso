package com.paonosso.app.data.api

import com.paonosso.app.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Injeta automaticamente o header `Authorization: Bearer <token>` quando ha
 * token disponivel. A leitura vai no cache em memoria exposto por
 * [TokenStore.cachedToken], evitando `runBlocking` e acesso a DataStore em
 * hot path (hidratamos o cache no boot via `AppNavHost.getToken()` e a cada
 * `save()`/`clear()`/mudanca do `tokenFlow`).
 */
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header("Authorization") != null) {
            return chain.proceed(original)
        }
        val token = tokenStore.cachedToken
        val request = if (token.isNullOrEmpty()) {
            original
        } else {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        }
        return chain.proceed(request)
    }
}
