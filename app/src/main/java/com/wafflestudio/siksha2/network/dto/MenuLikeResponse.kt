package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import java.time.LocalDate

data class MenuLikeResponse(
    val id: Long,
    @Json(name = "restaurant_id") val restaurantId: Long,
    val code: String,
    val date: LocalDate,
    val type: String,
    @Json(name = "name_kr") val nameKr: String?,
    @Json(name = "name_en") val nameEn: String?,
    val price: Int?,
    val etc: List<Any>?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    val score: Double?,
    @Json(name = "review_cnt") val reviewCount: Int,
    @Json(name = "is_liked") val isLiked: Boolean,
    @Json(name = "like_cnt") val likeCount: Int
)
