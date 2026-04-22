package com.paonosso.app.data

import android.content.Context
import com.paonosso.app.data.api.ApiClient
import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.local.TokenStore
import com.paonosso.app.data.repository.AppointmentRepository
import com.paonosso.app.data.repository.AuthRepository
import com.paonosso.app.data.repository.DonationRepository
import com.paonosso.app.data.repository.InstitutionRepository
import com.paonosso.app.data.repository.StatsRepository

/**
 * Service locator simples: criado uma vez pelo Application e consumido pelos
 * ViewModels via factory.
 */
class AppContainer(context: Context) {
    val tokenStore: TokenStore = TokenStore(context.applicationContext)
    val api: ApiService = ApiClient.get(context.applicationContext)

    val authRepository: AuthRepository = AuthRepository(api, tokenStore)
    val donationRepository: DonationRepository = DonationRepository(api)
    val institutionRepository: InstitutionRepository = InstitutionRepository(api)
    val appointmentRepository: AppointmentRepository = AppointmentRepository(api)
    val statsRepository: StatsRepository = StatsRepository(api)
}
