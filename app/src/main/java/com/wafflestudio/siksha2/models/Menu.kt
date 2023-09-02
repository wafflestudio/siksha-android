package com.wafflestudio.siksha2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "id") val id: Long,
    @Json(name = "code") val code: String,
    @Json(name = "date") val date: LocalDate?,
    @Json(name = "type") val type: MealsOfDay?,
    @Json(name = "restaurant_id") val restaurantId: Long?,
    @Json(name = "name_kr") val nameKr: String?,
    @Json(name = "name_en") val nameEn: String?,
    @Json(name = "price") val price: Long?,
    @Json(name = "score") val score: Double?,
    @Json(name = "etc") val etc: List<String>?,
    @Json(name = "review_cnt") val reviewCount: Long?,
    @Json(name = "is_liked") var isLiked: Boolean?,
    @Json(name = "like_cnt") var likeCount: Long?
) {
    @JsonClass(generateAdapter = true)
    data class Extra(
        @Json(name = "operating_hours") val operatingHours: RestaurantInfo.OperatingHour
    )
}

enum class MealsOfDay {
    @Json(name = "BR")
    BR,

    @Json(name = "LU")
    LU,

    @Json(name = "DN")
    DN
}
