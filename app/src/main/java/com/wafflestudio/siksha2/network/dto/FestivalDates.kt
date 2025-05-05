package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FestivalDates(
    @Json(name = "festival_dates") val festivalDates: List<String>
)
