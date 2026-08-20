package com.dip.a10swalkman

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.android.Android

object SupabaseClient {

    private const val SUPABASE_URL =
        "https://wsgehosguxntwhfefpwj.supabase.co"

    private const val SUPABASE_KEY =
        "sb_publishable_hRf2IAU91b_12caIdkAJAg_-K-27n9e"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {

        install(Auth)

        install(Postgrest)

        httpEngine = Android.create()
    }
}