package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveReviewParam(
    @Json(name = "menu_id") val menuId: Long,
    @Json(name = "score") val score: Long,
    @Json(name = "taste") val taste: String?,
    @Json(name = "price") val price: String?,
    @Json(name = "food_composition") val foodComposition: String?,
    @Json(name = "comment") val comment: String?
)
