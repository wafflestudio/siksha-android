package com.wafflestudio.siksha2.network.result

import java.io.IOException

sealed interface NetworkResult<out T : Any> {
    data class Success<T : Any>(val body: T) : NetworkResult<T>
    data class Failure(val message: String?) : NetworkResult<Nothing>
    data class NetworkError(val exception: IOException) : NetworkResult<Nothing>
    data class UnknownError(val t: Throwable?) : NetworkResult<Nothing>
}
