package com.example.avaliafilmes.data.api

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                ApiResult.Success(it)
            } ?: ApiResult.Error("Resposta vazia", response.code())
        } else {
            ApiResult.Error(
                message = response.message() ?: "Erro desconhecido",
                code = response.code()
            )
        }
    } catch (e: Exception) {
        when {
            e is java.net.UnknownHostException || 
            e is java.net.SocketTimeoutException ||
            e is java.io.IOException -> ApiResult.NetworkError
            else -> ApiResult.Error(e.message ?: "Erro ao processar requisição")
        }
    }
}
