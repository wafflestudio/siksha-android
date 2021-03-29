package com.wafflestudio.siksha2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuGroup(
    @Json(name = "id") val id: Long,
    @Json(name = "code") val restaurantCode: String,
    @Json(name = "name_kr") val nameKr: String?,
    @Json(name = "name_en") val nameEn: String?,
    @Json(name = "addr") val address: String?,
    @Json(name = "lat") val latitude: Double?,
    @Json(name = "lng") val longitude: Double?,
    @Json(name = "menus") val menus: List<Menu>,
    @Transient() val isFavorite: Boolean = false
)

fun MenuGroup.getRestaurantInfo(): RestaurantInfo {
    return RestaurantInfo(
        id,
        restaurantCode,
        nameKr,
        nameEn,
        address,
        latitude,
        longitude,
        null
    )
}
