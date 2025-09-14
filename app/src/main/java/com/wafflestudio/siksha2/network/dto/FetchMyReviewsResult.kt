package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.Review

@JsonClass(generateAdapter = true)
data class ReviewRestaurant(
    @Json(name = "restaurant_id") val restaurantId: String,
    @Json(name = "name_Kr") val nameKr: String,
    @Json(name = "name_En") val nameEn: String,
    @Json(name = "reviews") val reviews: List<Review>
)

@JsonClass(generateAdapter = true)
data class FetchMyReviewsResult(
    @Json(name = "total_count") val totalCount: Int,
    @Json(name = "has_next") val hasNext: Boolean,
    @Json(name = "result") val result: List<ReviewRestaurant>
)
