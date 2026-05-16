package com.example.quizapp_boukenze

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthHelper {

    suspend fun login(emailArg: String, passArg: String) = withContext(Dispatchers.IO) {
        val response = ApiConfig.callWithFallback { api ->
            api.login(LoginRequest(emailArg, passArg))
        }
        ApiConfig.authToken = "Bearer ${response.token}"
    }

    suspend fun register(emailArg: String, passArg: String, user: User) = withContext(Dispatchers.IO) {
        ApiConfig.callWithFallback { api ->
            api.register(UserRequest(emailArg, passArg, user.name, user.school))
        }
    }

    suspend fun saveScore(scoreIIR: Int, scoreGESI: Int, scoreIAII: Int, scoreGC: Int, scoreGI: Int, scoreGF: Int) = withContext(Dispatchers.IO) {
        val token = ApiConfig.authToken ?: throw Exception("Not logged in")
        val scoreData = UserScoreRequest(
            score_iir = scoreIIR,
            score_gesi = scoreGESI,
            score_iaii = scoreIAII,
            score_gc = scoreGC,
            score_gi = scoreGI,
            score_gf = scoreGF
        )
        ApiConfig.callWithFallback { api ->
            api.saveScore(token, scoreData)
        }
    }

    suspend fun getLatestScore(): UserScore? = withContext(Dispatchers.IO) {
        val token = ApiConfig.authToken ?: return@withContext null
        val response = ApiConfig.callWithFallback { api ->
            api.getLatestScore(token)
        } ?: return@withContext null
        UserScore(
            user_id = "local_user",
            score_iir = response.score_iir,
            score_gesi = response.score_gesi,
            score_iaii = response.score_iaii,
            score_gc = response.score_gc,
            score_gi = response.score_gi,
            score_gf = response.score_gf,
            created_at = response.createdAt
        )
    }

    suspend fun getQuizQuestions(): List<QuestionWithheld> = withContext(Dispatchers.IO) {
        val remoteQuestions = try {
            val response = ApiConfig.callWithFallback { api ->
                api.getQuestions()
            }
            response.map { q ->
                val question = Question(0, q.question_text, q.order_index)
                val options = q.options.mapIndexed { index, opt ->
                    QuestionOption(index, 0, opt.option_text, opt.target_major_code)
                }
                QuestionWithheld(question, options)
            }
        } catch (e: Exception) {
            emptyList()
        }

        QuizQuestionBank.completeWithDefaults(remoteQuestions)
    }
}
