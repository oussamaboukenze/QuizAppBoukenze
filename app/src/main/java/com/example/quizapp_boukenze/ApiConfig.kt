package com.example.quizapp_boukenze

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiConfig {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var activeBaseUrl: String = AppConfig.mongoApiBaseUrls().first()

    @Volatile
    private var activeApi: ApiService = createApi(activeBaseUrl)

    val api: ApiService
        get() = activeApi

    var authToken: String? = null

    suspend fun <T> callWithFallback(block: suspend (ApiService) -> T): T {
        var lastConnectionError: IOException? = null

        for (baseUrl in orderedBaseUrls()) {
            val service = if (baseUrl == activeBaseUrl) activeApi else createApi(baseUrl)
            try {
                val result = block(service)
                promote(baseUrl, service)
                return result
            } catch (e: IOException) {
                lastConnectionError = e
            }
        }

        throw lastConnectionError ?: IOException("Aucune API disponible")
    }

    private fun orderedBaseUrls(): List<String> {
        return (listOf(activeBaseUrl) + AppConfig.mongoApiBaseUrls()).distinct()
    }

    private fun promote(baseUrl: String, service: ApiService) {
        if (baseUrl != activeBaseUrl) {
            activeBaseUrl = baseUrl
            activeApi = service
        }
    }

    private fun createApi(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
