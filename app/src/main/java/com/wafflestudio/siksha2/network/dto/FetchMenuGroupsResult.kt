package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.MenuGroup

@JsonClass(generateAdapter = true)
data class FetchMenuGroupsResult(
    @Json(name = "result") val result: List<DailyMenuGroupResponse>,
    @Json(name = "count") val count: Int
) {

    @JsonClass(generateAdapter = true)
    data class DailyMenuGroupResponse(
        @Json(name = "date") val date: String,
        @Json(name = "BR") val breakfast: List<MenuGroup>,
        @Json(name = "LU") val lunch: List<MenuGroup>,
        @Json(name = "DN") val dinner: List<MenuGroup>
    )
}

fun FetchMenuGroupsResult.DailyMenuGroupResponse.filterEmpty(): FetchMenuGroupsResult.DailyMenuGroupResponse {
    return FetchMenuGroupsResult.DailyMenuGroupResponse(
        date,
        breakfast.filter { it.menus.isNotEmpty() },
        breakfast.filter { it.menus.isNotEmpty() },
        breakfast.filter { it.menus.isNotEmpty() }
    )
}
