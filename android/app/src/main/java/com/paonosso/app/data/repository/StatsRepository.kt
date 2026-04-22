package com.paonosso.app.data.repository

import com.paonosso.app.data.api.ApiService
import com.paonosso.app.data.model.Stats

class StatsRepository(private val api: ApiService) {
    suspend fun me(): Result<Stats> = runCatching { api.stats() }
}
