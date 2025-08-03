package com.devmosaic.watchit.model

import com.google.gson.annotations.SerializedName

// This data class will represent a single movie in the list from the /discover endpoint
data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterPath: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("vote_average")
    val voteAverage: Double,

    // You can add more fields from the API response as needed, like...
    @SerializedName("overview")
    val plot: String,

    @SerializedName("backdrop_path")
    val backdropPath: String,

    @SerializedName("genre_ids")
    val genreIds: List<Int>
)

// A separate data class for the top-level API response, as TMDB wraps the list in a "results" object
data class MovieResponse(
    @SerializedName("results")
    val movies: List<Movie>
)