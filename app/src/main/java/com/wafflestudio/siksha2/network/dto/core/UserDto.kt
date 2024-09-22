package com.wafflestudio.siksha2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Long,
    @Json(name = "type") val type: String,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "identity") val identity: String,
    @Json(name = "profile_url") val profileUrl: String?
)
