package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetFavoriteMenusResponse(
    val count: Int,
    val result: List<FavoriteRestaurantDto>
)

@JsonClass(generateAdapter = true)
data class FavoriteRestaurantDto(
    val id: Long,
    val code: String?,
    val name_kr: String,
    val name_en: String?,
    val addr: String?,
    val lat: Double?,
    val lng: Double?,
    val menus: List<FavoriteMenuDto>
)

@JsonClass(generateAdapter = true)
data class FavoriteMenuDto(
    val id: Long,
    val code: String?,
    val name_kr: String,
    val name_en: String?,
    val price: Int?,
    val etc: List<String>?,
    val score: Double?,
    val review_cnt: Int?,
    val like_cnt: Int?,
    val is_liked: Boolean,
    val alarm: Boolean? = null
)
