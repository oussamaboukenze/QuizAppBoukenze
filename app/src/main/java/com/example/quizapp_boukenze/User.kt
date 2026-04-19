package com.example.quizapp_boukenze

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val email: String,
    val name: String,
    val school: String,
    val latitude: Double,
    val longitude: Double
)