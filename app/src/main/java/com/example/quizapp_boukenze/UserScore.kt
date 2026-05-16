package com.example.quizapp_boukenze

data class UserScore(
    val user_id: String,
    val score_iir: Int,
    val score_gesi: Int,
    val score_iaii: Int,
    val score_gc: Int,
    val score_gi: Int,
    val score_gf: Int,
    val created_at: String
)
