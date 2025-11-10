package com.example.avaliafilmes.data.dao

import androidx.room.*
import com.example.avaliafilmes.data.model.MovieReview
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieReviewDao {
    @Insert
    suspend fun insert(review: MovieReview): Long
    
    @Query("SELECT * FROM movie_reviews WHERE userId = :userId ORDER BY timestamp DESC")
    fun getReviewsByUser(userId: Long): Flow<List<MovieReview>>
    
    @Query("SELECT * FROM movie_reviews WHERE imdbId = :imdbId ORDER BY timestamp DESC")
    fun getReviewsByMovie(imdbId: String): Flow<List<MovieReview>>
    
    @Query("SELECT * FROM movie_reviews WHERE userId = :userId AND imdbId = :imdbId LIMIT 1")
    suspend fun getUserReviewForMovie(userId: Long, imdbId: String): MovieReview?
    
    @Query("SELECT * FROM movie_reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<MovieReview>>
    
    @Update
    suspend fun update(review: MovieReview)
    
    @Delete
    suspend fun delete(review: MovieReview)
    
    @Query("SELECT AVG(rating) FROM movie_reviews WHERE imdbId = :imdbId")
    suspend fun getAverageRating(imdbId: String): Float?
}
