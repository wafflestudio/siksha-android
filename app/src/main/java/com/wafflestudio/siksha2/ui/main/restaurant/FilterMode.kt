package com.wafflestudio.siksha2.ui.main.restaurant

enum class FilterMode {
    FULL,
    DISTANCE,
    PRICE,
    OPEN,
    REVIEW,
    RATING,
    CATEGORY;

    companion object {
        fun fromPosition(position: Int): FilterMode {
            return FilterMode.values()[position]
        }
    }
}
