package com.example.avaliafilmes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_reviews")
data class MovieReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val imdbId: String,
    val movieTitle: String,
    val moviePoster: String,
    val movieYear: String,
    val rating: Float, // 0 a 5 estrelas
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
