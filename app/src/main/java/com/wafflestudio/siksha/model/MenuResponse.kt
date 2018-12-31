package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class MenuResponse(
        @Json(name = "today") val today: DailyMenu,
        @Json(name = "tomorrow") val tomorrow: DailyMenu
) {
    data class DailyMenu(
            @Json(name = "date") val date: String,
            @Json(name = "menus") val menus: List<Menu>
    )
}