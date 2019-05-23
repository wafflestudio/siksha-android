package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Meal(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "en_name") val enName: String,
    @field:Json(name = "kr_name") val krName: String,
    @field:Json(name = "restaurant") val restaurantId: Int,
    @field:Json(name = "score") val score: Double? = null,
    @field:Json(name = "score_count") val scoreCount: Int,
    @field:Json(name = "price") val price: String,
    @field:Json(name = "etc") val etc: String
)