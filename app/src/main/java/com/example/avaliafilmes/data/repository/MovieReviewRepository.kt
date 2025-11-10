package com.example.avaliafilmes.data.repository

import com.example.avaliafilmes.data.dao.MovieReviewDao
import com.example.avaliafilmes.data.model.MovieReview
import kotlinx.coroutines.flow.Flow

class MovieReviewRepository(private val movieReviewDao: MovieReviewDao) {
    
    suspend fun addReview(review: MovieReview): Result<Long> {
        return try {
            val reviewId = movieReviewDao.insert(review)
            Result.success(reviewId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getReviewsByUser(userId: Long): Flow<List<MovieReview>> {
        return movieReviewDao.getReviewsByUser(userId)
    }
    
    fun getReviewsByMovie(imdbId: String): Flow<List<MovieReview>> {
        return movieReviewDao.getReviewsByMovie(imdbId)
    }
    
    suspend fun getUserReviewForMovie(userId: Long, imdbId: String): MovieReview? {
        return movieReviewDao.getUserReviewForMovie(userId, imdbId)
    }
    
    fun getAllReviews(): Flow<List<MovieReview>> {
        return movieReviewDao.getAllReviews()
    }
    
    suspend fun updateReview(review: MovieReview): Result<Unit> {
        return try {
            movieReviewDao.update(review)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteReview(review: MovieReview): Result<Unit> {
        return try {
            movieReviewDao.delete(review)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAverageRating(imdbId: String): Float {
        return movieReviewDao.getAverageRating(imdbId) ?: 0f
    }
}
