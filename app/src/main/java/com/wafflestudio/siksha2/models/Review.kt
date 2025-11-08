package com.wafflestudio.siksha2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "id") val id: Long,
    @Json(name = "menu_id") val menuId: Long,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "score") val score: Double,
    @Json(name = "comment") val comment: String?,
    @Json(name = "etc") val etc: List<String>,
    @Json(name = "keyword_reviews") val keywordReviews: List<String>,
    @Json(name = "like_count") val likeCount: Long,
    @Json(name = "is_liked") val isLiked: Boolean,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class Etc(
    @Json(name = "images") val images: List<String>?
)
