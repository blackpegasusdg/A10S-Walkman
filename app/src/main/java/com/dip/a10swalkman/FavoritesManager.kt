package com.dip.a10swalkman

import android.content.Context

class FavoritesManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "a10s_favorites",
        Context.MODE_PRIVATE
    )

    private val FAVORITES_KEY = "favorite_song_ids"

    fun getFavorites(): Set<Long> {
        return prefs.getStringSet(
            FAVORITES_KEY,
            emptySet()
        )?.mapNotNull {
            it.toLongOrNull()
        }?.toSet() ?: emptySet()
    }

    fun isFavorite(songId: Long): Boolean {
        return getFavorites().contains(songId)
    }

    fun addFavorite(songId: Long) {

        val favorites = getFavorites().toMutableSet()

        favorites.add(songId)

        saveFavorites(favorites)
    }

    fun removeFavorite(songId: Long) {

        val favorites = getFavorites().toMutableSet()

        favorites.remove(songId)

        saveFavorites(favorites)
    }

    fun toggleFavorite(songId: Long): Boolean {

        val favorites = getFavorites().toMutableSet()

        val nowFavorite: Boolean

        if (favorites.contains(songId)) {

            favorites.remove(songId)

            nowFavorite = false

        } else {

            favorites.add(songId)

            nowFavorite = true
        }

        saveFavorites(favorites)

        return nowFavorite
    }

    private fun saveFavorites(
        favorites: Set<Long>
    ) {

        prefs.edit()
            .putStringSet(
                FAVORITES_KEY,
                favorites.map { it.toString() }.toSet()
            )
            .apply()
    }
}