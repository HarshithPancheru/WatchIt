package com.devmosaic.watchit.network

import com.devmosaic.watchit.model.Genre
import com.devmosaic.watchit.model.MovieDetail
import com.devmosaic.watchit.model.MovieResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


data class GenreResponse(
    val genres: List<Genre>
)

// New data classes for the /credits endpoint
data class CreditsResponse(val cast: List<CastMember>, val crew: List<CrewMember>)
data class CastMember(val name: String, @SerializedName("profile_path") val profilePath: String?)
data class CrewMember(val name: String, val job: String)


interface ApiService {

    // Add "movie/" to the path
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("with_genres") withGenres: String? = null,    // Comma-separated genre IDs
        @Query("without_genres") withoutGenres: String? = null // Comma-separated genre IDs
    ): MovieResponse

    // Add "movie/" to the path
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetail


    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String
    ): GenreResponse


    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse
}