package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserData(
    @Json(name = "nickname") val nickname: String?,
    @Json(name = "image") val image: String?
)
