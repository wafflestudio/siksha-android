package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveReviewParam(
    @Json(name = "menu_id") val menuId: Long,
    @Json(name = "score") val score: Double,
    @Json(name = "comment") val comment: String
)
