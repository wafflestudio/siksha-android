package com.wafflestudio.siksha2.ui.main.setting.reorder

import com.wafflestudio.siksha2.models.RestaurantInfo

sealed interface ReorderRestaurantUiState {
    object Idle : ReorderRestaurantUiState
    object Loading : ReorderRestaurantUiState
    data class Success(val restaurants: List<RestaurantInfo>) : ReorderRestaurantUiState
    data class Failure(val message: String) : ReorderRestaurantUiState
}
