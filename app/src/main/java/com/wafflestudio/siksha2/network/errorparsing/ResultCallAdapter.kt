package com.wafflestudio.siksha2.network.errorparsing

import com.wafflestudio.siksha2.preferences.serializer.Serializer
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultCallAdapter<T: Any>(
    private val responseType: Type,
    private val serializer: Serializer
): CallAdapter<T, Call<NetworkResult<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<NetworkResult<T>> = ResultCall(call, serializer)
}
