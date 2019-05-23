package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Review(
    @field:Json(name = "meal") val mealId: Int,
    @field:Json(name = "score") val score: Double
)

data class Reviews(
    val reviews: List<Review>
)