package com.devmosaic.watchit.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Create the DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        // We'll use this to define our score changes
        const val LIKE_SCORE_CHANGE = 5
        const val DISLIKE_SCORE_CHANGE = -5
    }

    // This function updates the score for a specific genre
    suspend fun updateGenreScore(genreId: Int, scoreChange: Int) {
        val preferenceKey = intPreferencesKey(genreId.toString())
        context.dataStore.edit { preferences ->
            val currentScore = preferences[preferenceKey] ?: 0
            preferences[preferenceKey] = currentScore + scoreChange
        }
    }

    // This function can be used later to retrieve all scores
    suspend fun getGenreScores(): Map<Int, Int> {
        val preferences = context.dataStore.data.first()
        return preferences.asMap().mapNotNull { (key, value) ->
            if (value is Int) {
                key.name.toIntOrNull()?.let { it to value }
            } else {
                null
            }
        }.toMap()
    }

    suspend fun resetPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
            Log.d("UserPreferencesRepo", "User preferences have been reset.")
        }
    }

}