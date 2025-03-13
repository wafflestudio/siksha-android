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

data class MenuFilterCondition(
    val distance: Float?,
    val minPrice: Float?,
    val maxPrice: Float?,
    val isOpen: Boolean,
    val hasReview: Boolean,
    val minRating: Float?,
    val categories: List<String>?
)
