package com.dip.a10swalkman

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AuthRepository {

    private val supabase = SupabaseClient.client

    suspend fun register(
        email: String,
        password: String,
        username: String,
        displayName: String
    ) {

        supabase.auth.signUpWith(Email) {

            this.email = email
            this.password = password

            data = buildJsonObject {
                put("username", username)
                put("display_name", displayName)
            }
        }
    }

    suspend fun login(
        email: String,
        password: String
    ) {

        supabase.auth.signInWith(Email) {

            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {

        supabase.auth.signOut()
    }

    fun currentUserId(): String? {

        return supabase.auth.currentUserOrNull()?.id
    }

    fun isLoggedIn(): Boolean {

        return supabase.auth.currentUserOrNull() != null
    }
}