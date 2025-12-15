package com.example.avaliafilmes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avaliafilmes.data.api.ApiResult
import com.example.avaliafilmes.data.model.MovieSearchResult
import com.example.avaliafilmes.data.model.OMDBMovieDetails
import com.example.avaliafilmes.data.service.OMDBService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val movies: List<MovieSearchResult>) : SearchState()
    data class Error(val message: String) : SearchState()
    object Empty : SearchState()
    object NetworkError : SearchState()
}

sealed class MovieDetailsState {
    object Idle : MovieDetailsState()
    object Loading : MovieDetailsState()
    data class Success(val movie: OMDBMovieDetails) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
    object NetworkError : MovieDetailsState()
}

class MovieViewModel(private val omdbService: OMDBService = OMDBService()) : ViewModel() {
    
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    
    private val _movieDetailsState = MutableStateFlow<MovieDetailsState>(MovieDetailsState.Idle)
    val movieDetailsState: StateFlow<MovieDetailsState> = _movieDetailsState.asStateFlow()
    
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Idle
            return
        }
        
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            
            when (val result = omdbService.searchMovies(query)) {
                is ApiResult.Success -> {
                    _searchState.value = if (result.data.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Success(result.data)
                    }
                }
                is ApiResult.Error -> {
                    _searchState.value = SearchState.Error(result.message)
                }
                is ApiResult.NetworkError -> {
                    _searchState.value = SearchState.NetworkError
                }
                is ApiResult.Loading -> {
                    _searchState.value = SearchState.Loading
                }
            }
        }
    }
    
    fun getMovieDetails(imdbId: String) {
        viewModelScope.launch {
            _movieDetailsState.value = MovieDetailsState.Loading
            
            when (val result = omdbService.getMovieDetails(imdbId)) {
                is ApiResult.Success -> {
                    _movieDetailsState.value = MovieDetailsState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _movieDetailsState.value = MovieDetailsState.Error(result.message)
                }
                is ApiResult.NetworkError -> {
                    _movieDetailsState.value = MovieDetailsState.NetworkError
                }
                is ApiResult.Loading -> {
                    _movieDetailsState.value = MovieDetailsState.Loading
                }
            }
        }
    }
    
    fun resetSearchState() {
        _searchState.value = SearchState.Idle
    }
    
    fun resetMovieDetailsState() {
        _movieDetailsState.value = MovieDetailsState.Idle
    }
}
