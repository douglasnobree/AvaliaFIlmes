package com.example.avaliafilmes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}

sealed class MovieDetailsState {
    object Idle : MovieDetailsState()
    object Loading : MovieDetailsState()
    data class Success(val movie: OMDBMovieDetails) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}

class MovieViewModel(private val omdbService: OMDBService) : ViewModel() {
    
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
            
            omdbService.searchMovies(query) { movies ->
                _searchState.value = when {
                    movies == null -> SearchState.Error("Erro ao buscar filmes")
                    movies.isEmpty() -> SearchState.Empty
                    else -> SearchState.Success(movies)
                }
            }
        }
    }
    
    fun getMovieDetails(imdbId: String) {
        viewModelScope.launch {
            _movieDetailsState.value = MovieDetailsState.Loading
            
            omdbService.getMovieDetails(imdbId) { movieDetails ->
                _movieDetailsState.value = if (movieDetails != null) {
                    MovieDetailsState.Success(movieDetails)
                } else {
                    MovieDetailsState.Error("Erro ao carregar detalhes do filme")
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
