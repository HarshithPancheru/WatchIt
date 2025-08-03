package com.devmosaic.watchit.screen.details

import com.devmosaic.watchit.model.MovieDetail
import com.devmosaic.watchit.network.CastMember

// This sealed interface represents all possible states of the Detail Screen UI
sealed interface DetailUiState {
    data object Loading : DetailUiState
    // Update the Success state to hold the new info
    data class Success(
        val details: MovieDetail,
        val director: String?,
        val cast: List<CastMember>
    ) : DetailUiState
    data object Error : DetailUiState
}