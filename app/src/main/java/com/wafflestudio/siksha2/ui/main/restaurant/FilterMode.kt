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
    val distance: Float,
    val minPrice: Float,
    val maxPrice: Float,
    val isOpen: Boolean,
    val hasReview: Boolean,
    val minRating: Float,
    val categories: Set<String>
) {
    companion object {
        val DEFAULT = MenuFilterCondition(
            distance = 1000f,
            minPrice = 2500f,
            maxPrice = 10000f,
            isOpen = false,
            hasReview = false,
            minRating = 0f,
            categories = emptySet()
        )
    }
}
