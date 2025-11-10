package com.example.avaliafilmes.data.model

data class OMDBSearchResponse(
    val Search: List<MovieSearchResult>?,
    val totalResults: String?,
    val Response: String
)

data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)
