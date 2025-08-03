package com.devmosaic.watchit.screen.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmosaic.watchit.data.UserPreferencesRepository
import com.devmosaic.watchit.network.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DetailViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the repository
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()


    fun fetchMovieDetails(movieId: Int) {
        Log.d("DetailViewModel", "Fetching details for movie ID: $movieId")
        _uiState.value = DetailUiState.Loading

        viewModelScope.launch {
            try {
                // Run both API calls in parallel for efficiency
                coroutineScope {
                    val detailsDeferred = async { RetrofitInstance.api.getMovieDetails(movieId, "0a820cf84e164dad35ac5c860cd44ba0") }
                    val creditsDeferred = async { RetrofitInstance.api.getMovieCredits(movieId, "0a820cf84e164dad35ac5c860cd44ba0") }

                    val details = detailsDeferred.await()
                    val credits = creditsDeferred.await()

                    // Find the director and get the top 5 cast members
                    val director = credits.crew.find { it.job == "Director" }?.name
                    val cast = credits.cast.take(5)

                    Log.d("DetailViewModel", "Successfully fetched: ${details.title}")
                    _uiState.value = DetailUiState.Success(details, director, cast)
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error fetching movie details: ", e)
                _uiState.value = DetailUiState.Error
            }
        }
    }



    fun onLikeClicked() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is DetailUiState.Success) {
                Log.d("DetailViewModel", "Liking movie: ${currentState.details.title}")
                currentState.details.genres.forEach { genre ->
                    preferencesRepository.updateGenreScore(genre.id, UserPreferencesRepository.LIKE_SCORE_CHANGE)
                    Log.d("DetailViewModel", "  +5 score for genre: ${genre.name}")
                }
            }
        }
    }

    fun onDislikeClicked() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is DetailUiState.Success) {
                Log.d("DetailViewModel", "Disliking movie: ${currentState.details.title}")
                currentState.details.genres.forEach { genre ->
                    preferencesRepository.updateGenreScore(genre.id, UserPreferencesRepository.DISLIKE_SCORE_CHANGE)
                    Log.d("DetailViewModel", "  -5 score for genre: ${genre.name}")
                }
            }
        }
    }
}