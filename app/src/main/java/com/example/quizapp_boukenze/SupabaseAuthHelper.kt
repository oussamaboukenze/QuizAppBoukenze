package com.example.quizapp_boukenze

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class UserScore(
    val user_id: String,
    val score_iir: Int,
    val score_gesi: Int,
    val score_iaii: Int,
    val score_gc: Int,
    val score_gi: Int,
    val score_gf: Int,
    val created_at: String? = null
)

object SupabaseAuthHelper {
    @JvmStatic
    fun login(emailArg: String, passArg: String) {
        runBlocking {
            withContext(Dispatchers.IO) {
                SupabaseConfig.client.auth.signInWith(Email) {
                    email = emailArg
                    password = passArg
                }
            }
        }
    }

    @JvmStatic
    fun register(emailArg: String, passArg: String, user: User) {
        runBlocking {
            withContext(Dispatchers.IO) {
                SupabaseConfig.client.auth.signUpWith(Email) {
                    email = emailArg
                    password = passArg
                }
                
                val currentUser = SupabaseConfig.client.auth.currentUserOrNull() 
                
                if (currentUser != null) {
                    val userWithId = user.copy(id = currentUser.id)
                    SupabaseConfig.client.postgrest.from("users").insert(userWithId)
                } else {
                    throw Exception("Inscription réussie ! Veuillez vérifier votre e-mail pour confirmer votre compte avant de continuer.")
                }
            }
        }
    }

    @JvmStatic
    fun saveScore(scoreIIR: Int, scoreGESI: Int, scoreIAII: Int, scoreGC: Int, scoreGI: Int, scoreGF: Int) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val user = SupabaseConfig.client.auth.currentUserOrNull()
                if (user != null) {
                    val scoreData = UserScore(
                        user_id = user.id,
                        score_iir = scoreIIR,
                        score_gesi = scoreGESI,
                        score_iaii = scoreIAII,
                        score_gc = scoreGC,
                        score_gi = scoreGI,
                        score_gf = scoreGF,
                        created_at = java.time.Instant.now().toString()
                    )
                    SupabaseConfig.client.postgrest.from("scores").insert(scoreData)
                }
            }
        }
    }

    @JvmStatic
    fun getLatestScore(): UserScore? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val user = SupabaseConfig.client.auth.currentUserOrNull()
                if (user != null) {
                    try {
                        SupabaseConfig.client.postgrest.from("scores")
                            .select {
                                filter {
                                    eq("user_id", user.id)
                                }
                                order("created_at", Order.DESCENDING)
                                limit(1)
                            }.decodeSingleOrNull<UserScore>()
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }

    @JvmStatic
    fun getQuizQuestions(): List<QuestionWithheld> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                try {
                    val questions = SupabaseConfig.client.postgrest.from("questions")
                        .select {
                            order("order_index", Order.ASCENDING)
                        }.decodeList<Question>()
                    
                    val options = SupabaseConfig.client.postgrest.from("question_options")
                        .select().decodeList<QuestionOption>()
                    
                    questions.map { q ->
                        QuestionWithheld(q, options.filter { it.question_id == q.id })
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
    }
}
