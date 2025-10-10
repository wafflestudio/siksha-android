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
    @Json(name = "keywordReviews") val keywordReviews: List<String>?,
    @Json(name = "likeCount") val likeCount: Int?,
    @Json(name = "isLiked") val isLiked: Boolean?,
    @Json(name = "nameKr") val nameKr: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "etc") val etc: Etc?
)

@JsonClass(generateAdapter = true)
data class Etc(
    @Json(name = "images") val images: List<String>?
)
