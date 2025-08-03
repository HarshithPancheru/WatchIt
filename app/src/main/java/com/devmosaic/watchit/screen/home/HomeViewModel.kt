package com.devmosaic.watchit.screen.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devmosaic.watchit.data.UserPreferencesRepository
import com.devmosaic.watchit.model.Movie
import com.devmosaic.watchit.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = UserPreferencesRepository(application)
    private var genreMap = mapOf<Int, String>()

    // ViewModel State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // Internal tracking for pagination
    private var currentPage = 1
    private var dislikedGenreIds = ""
    private var likedGenreIds = ""

    init {
        // Fetch genre names once, then fetch the initial movie feed
        viewModelScope.launch {
            fetchGenreNames()
            fetchInitialFeed()
        }
    }

    private suspend fun fetchGenreNames() {
        try {
            val response = RetrofitInstance.api.getGenres(apiKey = "0a820cf84e164dad35ac5c860cd44ba0")
            genreMap = response.genres.associateBy({ it.id }, { it.name })
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to fetch genre names", e)
        }
    }

    private suspend fun fetchInitialFeed(isRefresh: Boolean = false) {
        if (isRefresh) {
            _uiState.update { it.copy(isRefreshing = true) }
        } else {
            _uiState.update { it.copy(isLoading = true) }
        }

        // 1. Get latest user preferences
        val genreScores = preferencesRepository.getGenreScores()
        updatePreferenceScoresForDialog(genreScores)
        likedGenreIds = genreScores.filter { it.value > 0 }.keys.joinToString(",")
        dislikedGenreIds = genreScores.filter { it.value < 0 }.keys.joinToString(",")

        val initialMovieList = mutableListOf<Movie>()
        var recommendationReason: String? = "Trending Movies"

        try {
            // 2. Fetch recommended movies if liked genres exist
            if (likedGenreIds.isNotEmpty()) {
                val recommendedResponse = RetrofitInstance.api.discoverMovies(
                    apiKey = "0a820cf84e164dad35ac5c860cd44ba0",
                    page = 1,
                    withGenres = likedGenreIds
                )
                initialMovieList.addAll(recommendedResponse.movies)
                recommendationReason = "Because you like our recommendations"
            }

            // 3. Fetch the first page of "explore" movies (excluding all rated genres)
            val allRatedGenreIds = genreScores.keys.joinToString(",")
            val exploreResponse = RetrofitInstance.api.discoverMovies(
                apiKey = "0a820cf84e164dad35ac5c860cd44ba0",
                page = 1,
                withoutGenres = if (allRatedGenreIds.isEmpty()) null else allRatedGenreIds
            )
            initialMovieList.addAll(exploreResponse.movies)
            currentPage = 1 // Reset page number

            // Update state with the combined initial list
            _uiState.update {
                it.copy(
                    movies = initialMovieList.distinctBy { movie -> movie.id }, // Remove duplicates
                    recommendationReason = recommendationReason
                )
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching initial feed", e)
        } finally {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    fun loadMoreMovies() {
        // Prevent multiple calls while already loading
        if (_uiState.value.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            currentPage++
            Log.d("HomeViewModel", "Loading more movies, page: $currentPage")

            try {
                val allRatedGenreIds = (likedGenreIds.split(",") + dislikedGenreIds.split(",")).joinToString(",")
                val newMoviesResponse = RetrofitInstance.api.discoverMovies(
                    apiKey = "0a820cf84e164dad35ac5c860cd44ba0",
                    page = currentPage,
                    withoutGenres = if (allRatedGenreIds.isBlank()) null else allRatedGenreIds
                )

                // Append new movies to the existing list
                _uiState.update { currentState ->
                    val updatedList = (currentState.movies + newMoviesResponse.movies).distinctBy { it.id }
                    currentState.copy(movies = updatedList)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading more movies", e)
            } finally {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            fetchInitialFeed(isRefresh = true)
        }
    }

    fun resetUserPreferences() {
        viewModelScope.launch {
            preferencesRepository.resetPreferences()
            fetchInitialFeed(isRefresh = true)
        }
    }

    private fun updatePreferenceScoresForDialog(scores: Map<Int, Int>) {
        val mappedScores = scores.mapNotNull { (id, score) ->
            genreMap[id]?.let { name -> name to score }
        }.toMap()
        _uiState.update { it.copy(preferenceScores = mappedScores) }
    }
}

// Define a UI State data class to hold all state properties cleanly
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val recommendationReason: String? = null,
    val preferenceScores: Map<String, Int> = emptyMap()
)