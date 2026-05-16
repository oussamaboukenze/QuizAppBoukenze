package com.example.quizapp_boukenze

object AppConfig {
    @JvmStatic
    fun mongoApiBaseUrl(): String = normalizeBaseUrl(BuildConfig.MONGO_API_BASE_URL)

    @JvmStatic
    fun mongoApiBaseUrls(): List<String> {
        val port = BuildConfig.MONGO_API_PORT
        return listOf(
            mongoApiBaseUrl(),
            "http://10.0.2.2:$port/",
            "http://127.0.0.1:$port/"
        )
            .map { normalizeBaseUrl(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    @JvmStatic
    fun ollamaUsbBaseUrl(): String = normalizeBaseUrl(BuildConfig.OLLAMA_USB_BASE_URL)

    @JvmStatic
    fun ollamaLanBaseUrl(): String = normalizeBaseUrl(BuildConfig.OLLAMA_LAN_BASE_URL)

    @JvmStatic
    fun ollamaPort(): Int = BuildConfig.OLLAMA_PORT

    @JvmStatic
    fun ollamaBaseUrls(discoveredBaseUrl: String?): List<String> {
        return listOf(
            ollamaUsbBaseUrl(),
            discoveredBaseUrl.orEmpty(),
            ollamaLanBaseUrl()
        )
            .map { normalizeBaseUrl(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    private fun normalizeBaseUrl(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) {
            return trimmed
        }
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}
