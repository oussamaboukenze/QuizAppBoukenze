package com.example.quizapp_boukenze

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    val client = createSupabaseClient(
        supabaseUrl = "https://ykqebwjthbmvjkkpoagf.supabase.co",
        supabaseKey = "sb_publishable_dUfXoebboZaSkcthRhxC-Q_Wz47K-J7"
    ) {
        install(Auth)
        install(Postgrest)
    }
}