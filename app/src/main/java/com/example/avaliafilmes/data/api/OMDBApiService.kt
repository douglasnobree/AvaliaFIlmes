package com.example.avaliafilmes.data.api

import com.example.avaliafilmes.data.model.OMDBMovieDetails
import com.example.avaliafilmes.data.model.OMDBSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBApiService {
    
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String = "69731eec",
        @Query("s") searchQuery: String
    ): Response<OMDBSearchResponse>
    
    @GET("/")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String = "69731eec",
        @Query("i") imdbId: String
    ): Response<OMDBMovieDetails>
}
