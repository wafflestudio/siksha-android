package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Menu(
        @field:Json(name = "id") val id: Int,
        @field:Json(name = "restaurant") val restaurant: Restaurant,
        @field:Json(name = "meals") val meals: List<Meal>,
        @field:Json(name = "type") private val typeString: String
) {
    enum class Type(val value: String) {
        BREAKFAST("BR"),
        LUNCH("LU"),
        DINNER("DN")
    }

    val type: Type
        get() = when (typeString) {
            Type.BREAKFAST.value -> Type.BREAKFAST
            Type.LUNCH.value -> Type.LUNCH
            Type.DINNER.value -> Type.DINNER
            else -> throw Exception("Menu has no type")
        }
}
