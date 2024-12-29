package com.wafflestudio.siksha2.network.result

import java.io.IOException

sealed interface NetworkResult<out T : Any> {
    data class Success<T : Any>(val body: T) : NetworkResult<T>
    data class Failure(val message: String) : NetworkResult<Nothing>
    data class NetworkError(val exception: IOException) : NetworkResult<Nothing>
    data class UnknownError(val t: Throwable?) : NetworkResult<Nothing>

    fun <R : Any> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(body))
        is Failure -> this
        is NetworkError -> this
        is UnknownError -> this
    }

    fun onSuccess(action: (value: T) -> Unit): NetworkResult<T> = apply {
        if (this is Success) action(body)
    }

    fun onFailure(action: (message: String) -> Unit): NetworkResult<T> = apply {
        if (this is Failure) action(message)
    }

    fun onNetworkError(action: (exception: IOException) -> Unit): NetworkResult<T> = apply {
        if (this is NetworkError) action(exception)
    }

    fun onUnknownError(action: (exception: Throwable?) -> Unit): NetworkResult<T> = apply {
        if (this is UnknownError) action(t)
    }

    fun onError(action: (t: Throwable?) -> Unit): NetworkResult<T> = apply {
        when (this) {
            is NetworkError -> action(exception)
            is UnknownError -> action(t)
            else -> Unit
        }
    }
}
