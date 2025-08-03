package com.devmosaic.watchit.model

import com.google.gson.annotations.SerializedName

// This represents the full details of a single movie
data class MovieDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val plot: String,

    @SerializedName("poster_path")
    val posterPath: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("genres")
    val genres: List<Genre>, // A list of genre objects

    @SerializedName("runtime")
    val runtime: Int?, // e.g., 148 (minutes)

    @SerializedName("vote_average")
    val rating: Double
)

// The API returns genres as a list of objects, each with an id and name
data class Genre(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)