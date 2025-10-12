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
        @Json(name = "br") val breakfast: List<MenuGroup> = emptyList(),
        @Json(name = "lu") val lunch: List<MenuGroup> = emptyList(),
        @Json(name = "dn") val dinner: List<MenuGroup> = emptyList()
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
