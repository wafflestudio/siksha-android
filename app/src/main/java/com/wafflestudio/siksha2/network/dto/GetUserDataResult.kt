package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserDataResult(
    @Json(name = "id") val id: Long,
    @Json(name = "type") val type: String,
    @Json(name = "identity") val identity: String
)
