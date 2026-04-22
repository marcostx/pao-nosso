package com.paonosso.app.data.repository

import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.model.Institution

class InstitutionRepository(private val api: ApiService) {
    suspend fun list(bairro: String? = null): Result<List<Institution>> = runCatching {
        api.listInstitutions(bairro)
    }

    suspend fun mine(): Result<Institution> = runCatching { api.myInstitution() }

    suspend fun create(payload: Map<String, String?>): Result<Institution> = runCatching {
        api.createInstitution(payload)
    }
}
