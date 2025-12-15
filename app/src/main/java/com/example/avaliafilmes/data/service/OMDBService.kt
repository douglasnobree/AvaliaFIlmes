package com.example.avaliafilmes.data.service

import com.example.avaliafilmes.data.api.ApiResult
import com.example.avaliafilmes.data.api.RetrofitClient
import com.example.avaliafilmes.data.api.safeApiCall
import com.example.avaliafilmes.data.model.OMDBMovieDetails
import com.example.avaliafilmes.data.model.MovieSearchResult

class OMDBService {
    private val apiService = RetrofitClient.omdbApiService
    
    suspend fun searchMovies(query: String): ApiResult<List<MovieSearchResult>> {
        val result = safeApiCall { apiService.searchMovies(searchQuery = query) }
        
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.Response == "True" && result.data.Search != null) {
                    ApiResult.Success(result.data.Search)
                } else {
                    ApiResult.Success(emptyList())
                }
            }
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
            is ApiResult.Loading -> result
        }
    }
    
    suspend fun getMovieDetails(imdbId: String): ApiResult<OMDBMovieDetails> {
        val result = safeApiCall { apiService.getMovieDetails(imdbId = imdbId) }
        
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.Response == "True") {
                    result
                } else {
                    ApiResult.Error("Filme não encontrado")
                }
            }
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
            is ApiResult.Loading -> result
        }
    }
}
