package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.Review

@JsonClass(generateAdapter = true)
data class FetchReviewsResult(
    @Json(name = "total_count") val totalCount: Long,
    @Json(name = "result") val result: List<Review>
)
