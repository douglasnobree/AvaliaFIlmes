package com.example.avaliafilmes.data.service

import com.example.avaliafilmes.data.model.OMDBMovieDetails
import com.example.avaliafilmes.data.model.OMDBSearchResponse
import com.example.avaliafilmes.data.model.MovieSearchResult
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class OMDBService {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val apiKey = "69731eec"
    private val baseUrl = "https://www.omdbapi.com/"

    fun searchMovies(query: String, callback: (List<MovieSearchResult>?) -> Unit) {
        val url = "${baseUrl}?apikey=${apiKey}&s=${query}"
        
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val searchResponse = gson.fromJson(responseBody, OMDBSearchResponse::class.java)
                            if (searchResponse.Response == "True") {
                                callback(searchResponse.Search)
                            } else {
                                callback(emptyList())
                            }
                        } catch (e: Exception) {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }

    fun getMovieDetails(imdbId: String, callback: (OMDBMovieDetails?) -> Unit) {
        val url = "${baseUrl}?apikey=${apiKey}&i=${imdbId}"
        
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val movieDetails = gson.fromJson(responseBody, OMDBMovieDetails::class.java)
                            if (movieDetails.Response == "True") {
                                callback(movieDetails)
                            } else {
                                callback(null)
                            }
                        } catch (e: Exception) {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
}
