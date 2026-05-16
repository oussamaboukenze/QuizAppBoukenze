package com.example.quizapp_boukenze

import retrofit2.http.*

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body user: UserRequest): Any

    @POST("api/auth/login")
    suspend fun login(@Body creds: LoginRequest): LoginResponse

    @GET("api/questions")
    suspend fun getQuestions(): List<QuestionResponse>

    @POST("api/scores")
    suspend fun saveScore(@Header("Authorization") token: String, @Body score: UserScoreRequest): Any

    @GET("api/scores/latest")
    suspend fun getLatestScore(@Header("Authorization") token: String): UserScoreResponse?
}

data class UserRequest(val email: String, val password: String, val name: String, val school: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: UserInfo)
data class UserInfo(val id: String, val email: String, val name: String)

data class QuestionResponse(
    val _id: String,
    val question_text: String,
    val order_index: Int,
    val options: List<OptionResponse>
)
data class OptionResponse(val option_text: String, val target_major_code: String)

data class UserScoreRequest(
    val score_iir: Int,
    val score_gesi: Int,
    val score_iaii: Int,
    val score_gc: Int,
    val score_gi: Int,
    val score_gf: Int
)

data class UserScoreResponse(
    val _id: String,
    val score_iir: Int,
    val score_gesi: Int,
    val score_iaii: Int,
    val score_gc: Int,
    val score_gi: Int,
    val score_gf: Int,
    val createdAt: String
)
