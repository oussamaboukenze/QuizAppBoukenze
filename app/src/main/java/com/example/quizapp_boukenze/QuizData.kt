package com.example.quizapp_boukenze

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    @SerialName("question_id")
    val id: Int,
    val question_text: String,
    val order_index: Int
)

@Serializable
data class QuestionOption(
    @SerialName("option_id")
    val id: Int,
    @SerialName("question_id")
    val question_id: Int,
    val option_text: String,
    val target_major_code: String
)

data class QuestionWithheld(
    val question: Question,
    val options: List<QuestionOption>
)
