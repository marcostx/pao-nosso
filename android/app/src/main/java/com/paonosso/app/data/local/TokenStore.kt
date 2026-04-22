package com.paonosso.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pao_nosso_prefs")

class TokenStore(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("access_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_TIPO = stringPreferencesKey("user_tipo")
        val USER_NOME = stringPreferencesKey("user_nome")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val tipoFlow: Flow<String?> = context.dataStore.data.map { it[Keys.USER_TIPO] }
    val nomeFlow: Flow<String?> = context.dataStore.data.map { it[Keys.USER_NOME] }

    suspend fun getToken(): String? = context.dataStore.data.map { it[Keys.TOKEN] }.first()
    suspend fun getTipo(): String? = context.dataStore.data.map { it[Keys.USER_TIPO] }.first()
    suspend fun getNome(): String? = context.dataStore.data.map { it[Keys.USER_NOME] }.first()
    suspend fun getEmail(): String? = context.dataStore.data.map { it[Keys.USER_EMAIL] }.first()
    suspend fun getUserId(): String? = context.dataStore.data.map { it[Keys.USER_ID] }.first()

    suspend fun save(
        token: String,
        userId: String,
        tipo: String,
        nome: String?,
        email: String?,
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOKEN] = token
            prefs[Keys.USER_ID] = userId
            prefs[Keys.USER_TIPO] = tipo
            if (nome != null) prefs[Keys.USER_NOME] = nome
            if (email != null) prefs[Keys.USER_EMAIL] = email
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
