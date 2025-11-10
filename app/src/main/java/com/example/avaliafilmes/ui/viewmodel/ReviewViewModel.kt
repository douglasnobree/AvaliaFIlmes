package com.example.avaliafilmes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avaliafilmes.data.model.MovieReview
import com.example.avaliafilmes.data.repository.MovieReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReviewState {
    object Idle : ReviewState()
    object Loading : ReviewState()
    object Success : ReviewState()
    data class Error(val message: String) : ReviewState()
}

class ReviewViewModel(private val reviewRepository: MovieReviewRepository) : ViewModel() {
    
    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val reviewState: StateFlow<ReviewState> = _reviewState.asStateFlow()
    
    private val _userReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val userReviews: StateFlow<List<MovieReview>> = _userReviews.asStateFlow()
    
    private val _movieReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val movieReviews: StateFlow<List<MovieReview>> = _movieReviews.asStateFlow()
    
    private val _allReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val allReviews: StateFlow<List<MovieReview>> = _allReviews.asStateFlow()
    
    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()
    
    fun addReview(
        userId: Long,
        imdbId: String,
        movieTitle: String,
        moviePoster: String,
        movieYear: String,
        rating: Float,
        comment: String
    ) {
        viewModelScope.launch {
            _reviewState.value = ReviewState.Loading
            
            val review = MovieReview(
                userId = userId,
                imdbId = imdbId,
                movieTitle = movieTitle,
                moviePoster = moviePoster,
                movieYear = movieYear,
                rating = rating,
                comment = comment
            )
            
            val result = reviewRepository.addReview(review)
            
            result.fold(
                onSuccess = {
                    _reviewState.value = ReviewState.Success
                },
                onFailure = { exception ->
                    _reviewState.value = ReviewState.Error(exception.message ?: "Erro ao adicionar avaliação")
                }
            )
        }
    }
    
    fun loadUserReviews(userId: Long) {
        viewModelScope.launch {
            reviewRepository.getReviewsByUser(userId).collect { reviews ->
                _userReviews.value = reviews
            }
        }
    }
    
    fun loadMovieReviews(imdbId: String) {
        viewModelScope.launch {
            reviewRepository.getReviewsByMovie(imdbId).collect { reviews ->
                _movieReviews.value = reviews
            }
            
            val avgRating = reviewRepository.getAverageRating(imdbId)
            _averageRating.value = avgRating
        }
    }
    
    fun loadAllReviews() {
        viewModelScope.launch {
            reviewRepository.getAllReviews().collect { reviews ->
                _allReviews.value = reviews
            }
        }
    }
    
    suspend fun getUserReviewForMovie(userId: Long, imdbId: String): MovieReview? {
        return reviewRepository.getUserReviewForMovie(userId, imdbId)
    }
    
    fun resetReviewState() {
        _reviewState.value = ReviewState.Idle
    }
}
