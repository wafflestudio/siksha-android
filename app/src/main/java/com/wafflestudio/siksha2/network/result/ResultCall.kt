package com.wafflestudio.siksha2.network.result

import com.wafflestudio.siksha2.network.dto.core.ErrorDto
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

class ResultCall<T: Any>(
    private val call: Call<T>,
    private val serializer: Serializer
) : Call<NetworkResult<T>> {
    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        call.enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()?.string()

                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(NetworkResult.Success(body))
                        )
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(NetworkResult.UnknownError(IllegalStateException("body is null")))
                        )
                    }
                } else {
                    if(error == null) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(NetworkResult.UnknownError(IllegalStateException("errorbody is null")))
                        )
                        return
                    }

                    val errorDto = parseError(error)
                    if (errorDto == null) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(NetworkResult.UnknownError(IllegalStateException("errorbody parsing failed")))
                        )
                        return
                    }

                    callback.onResponse(
                        this@ResultCall,
                        Response.success(NetworkResult.Failure(errorDto.detail))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val errorResponse = when(t) {
                    is IOException -> NetworkResult.NetworkError(t)
                    else -> NetworkResult.UnknownError(t)
                }
                callback.onResponse(this@ResultCall, Response.success(errorResponse))
            }
        })
    }

    private fun parseError(errorBody: String): ErrorDto? {
        val errorDto = runCatching {
            serializer.deserialize<ErrorDto>(
                errorBody,
                ErrorDto::class.java
            )
        }.getOrNull()

        return errorDto
    }

    override fun clone(): Call<NetworkResult<T>> = ResultCall(call.clone(), serializer)

    override fun execute(): Response<NetworkResult<T>> = throw UnsupportedOperationException("ResultCall doesn't use execute()")

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}
