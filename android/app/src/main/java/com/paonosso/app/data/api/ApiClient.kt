package com.paonosso.app.data.api

import android.content.Context
import com.paonosso.app.BuildConfig
import com.paonosso.app.data.local.TokenStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile private var service: ApiService? = null

    fun get(context: Context): ApiService {
        val current = service
        if (current != null) return current
        synchronized(this) {
            val again = service
            if (again != null) return again
            val tokenStore = TokenStore(context.applicationContext)
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenStore))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            val built = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
            service = built
            return built
        }
    }
}
