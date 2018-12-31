package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Meal(
        @Json(name = "id") val id: Int,
        @Json(name = "en_name") val enName: String,
        @Json(name = "kr_name") val krName: String,
        @Json(name = "restaurant") val restaurantId: Int,
        @Json(name = "score") val score: Double? = null,
        @Json(name = "score_count") val scoreCount: Int,
        @Json(name = "price") val price: String,
        @Json(name = "etc") val etc: String
)