package com.paonosso.app.data.repository

import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.model.CreateDonationRequest
import com.paonosso.app.data.model.Donation

class DonationRepository(private val api: ApiService) {

    suspend fun create(request: CreateDonationRequest): Result<Donation> = runCatching {
        api.createDonation(request)
    }

    suspend fun listAvailable(
        bairro: String? = null,
        categoria: String? = null,
        janela: String? = null,
    ): Result<List<Donation>> = runCatching {
        api.listAvailableDonations(bairro = bairro, categoria = categoria, janela = janela)
    }

    suspend fun listMine(): Result<List<Donation>> = runCatching { api.listMyDonations() }

    suspend fun delete(id: String): Result<Unit> = runCatching {
        api.deleteDonation(id); Unit
    }
}
