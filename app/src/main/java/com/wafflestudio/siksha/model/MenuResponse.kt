package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class MenuResponse(
        @field:Json(name = "today") val today: DailyMenu,
        @field:Json(name = "tomorrow") val tomorrow: DailyMenu
) {
    data class DailyMenu(
            @field:Json(name = "date") val date: String,
            @field:Json(name = "menus") val menus: List<Menu>
    )
}