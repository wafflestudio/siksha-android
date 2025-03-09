package com.wafflestudio.siksha2.ui.main.restaurant

enum class FilterMode {
    FULL,
    DISTANCE,
    PRICE,
    RATING,
    CATEGORY;

    companion object {
        fun fromPosition(position: Int): FilterMode {
            return FilterMode.values()[position]
        }
    }
}
