package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FestivalDateCheckResponse(
    @Json(name = "target_date") val targetDate: String,
    @Json(name = "is_festival") val isFestival: Boolean
)
