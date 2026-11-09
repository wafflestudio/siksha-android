package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.RestaurantInfo

data class FetchPersonalRestaurantsResult(
    val count: Long,
    val result: List<PersonalRestaurantDto>
)

@JsonClass(generateAdapter = true)
data class PersonalRestaurantDto(
    val id: Long,
    val code: String,
    val nameKr: String? = null,
    val nameEn: String? = null,
    @Json(name = "addr") val address: String? = null,
    @Json(name = "lat") val latitude: Double? = null,
    @Json(name = "lng") val longitude: Double? = null,
    val liked: Boolean = false,
    val visible: Boolean = true,
    val etc: RestaurantInfo.Extra? = null
) {
    fun toRestaurantInfo(): RestaurantInfo =
        RestaurantInfo(
            id = id,
            restaurantCode = code,
            nameKr = nameKr,
            nameEn = nameEn,
            address = address,
            latitude = latitude,
            longitude = longitude,
            etc = etc,
            isFavorite = liked,
            visible = visible
        )
}
