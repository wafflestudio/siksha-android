package com.wafflestudio.siksha2.ui.main.setting.reorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReorderRestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ReorderRestaurantUiState>(ReorderRestaurantUiState.Idle)
    val uiState: StateFlow<ReorderRestaurantUiState> = _uiState

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    private val updatingRestaurantIds = mutableSetOf<Long>()

    fun loadRestaurants(onlyFavorites: Boolean) {
        viewModelScope.launch {
            _uiState.value = ReorderRestaurantUiState.Loading
            _uiState.value =
                when (val response = restaurantRepository.fetchPersonalRestaurants()) {
                    is NetworkResult.Success -> {
                        val restaurants = if (onlyFavorites) {
                            response.body.filter { it.isFavorite }
                        } else {
                            response.body
                        }
                        ReorderRestaurantUiState.Success(restaurants)
                    }
                    is NetworkResult.Failure -> ReorderRestaurantUiState.Failure(response.message)
                    is NetworkResult.NetworkError ->
                        ReorderRestaurantUiState.Failure(NETWORK_ERROR_MESSAGE)
                    is NetworkResult.UnknownError ->
                        ReorderRestaurantUiState.Failure(UNKNOWN_ERROR_MESSAGE)
                }
        }
    }

    fun toggleFavorite(restaurantId: Long) {
        val restaurant = currentRestaurants().find { it.id == restaurantId } ?: return
        if (!updatingRestaurantIds.add(restaurantId)) return

        val previousFavorite = restaurant.isFavorite
        val previousVisible = restaurant.visible
        val requestedFavorite = !previousFavorite
        val requestedVisible = previousVisible || requestedFavorite
        updateRestaurantState(restaurantId, requestedFavorite, requestedVisible)

        viewModelScope.launch {
            if (requestedVisible != previousVisible) {
                val visibleResponse = restaurantRepository.setPersonalRestaurantVisibleById(
                    id = restaurantId,
                    visible = requestedVisible
                )
                if (visibleResponse !is NetworkResult.Success) {
                    updateRestaurantState(restaurantId, previousFavorite, previousVisible)
                    emitError(visibleResponse)
                    updatingRestaurantIds.remove(restaurantId)
                    return@launch
                }
            }

            val favoriteResponse = restaurantRepository.setPersonalRestaurantFavoriteById(
                id = restaurantId,
                isFavorite = requestedFavorite
            )
            if (favoriteResponse is NetworkResult.Success) {
                updateRestaurantState(
                    restaurantId = restaurantId,
                    isFavorite = favoriteResponse.body.liked,
                    visible = requestedVisible
                )
            } else {
                updateRestaurantState(
                    restaurantId = restaurantId,
                    isFavorite = previousFavorite,
                    visible = requestedVisible
                )
                emitError(favoriteResponse)
            }
            updatingRestaurantIds.remove(restaurantId)
        }
    }

    fun toggleVisible(restaurantId: Long) {
        val restaurant = currentRestaurants().find { it.id == restaurantId } ?: return
        if (!updatingRestaurantIds.add(restaurantId)) return

        val previousFavorite = restaurant.isFavorite
        val previousVisible = restaurant.visible
        val requestedVisible = !previousVisible
        val requestedFavorite = previousFavorite && requestedVisible
        updateRestaurantState(restaurantId, requestedFavorite, requestedVisible)

        viewModelScope.launch {
            if (requestedFavorite != previousFavorite) {
                val favoriteResponse = restaurantRepository.setPersonalRestaurantFavoriteById(
                    id = restaurantId,
                    isFavorite = requestedFavorite
                )
                if (favoriteResponse !is NetworkResult.Success) {
                    updateRestaurantState(restaurantId, previousFavorite, previousVisible)
                    emitError(favoriteResponse)
                    updatingRestaurantIds.remove(restaurantId)
                    return@launch
                }
            }

            val visibleResponse = restaurantRepository.setPersonalRestaurantVisibleById(
                id = restaurantId,
                visible = requestedVisible
            )
            if (visibleResponse is NetworkResult.Success) {
                updateRestaurantState(
                    restaurantId = restaurantId,
                    isFavorite = requestedFavorite,
                    visible = visibleResponse.body.visible
                )
            } else {
                updateRestaurantState(
                    restaurantId = restaurantId,
                    isFavorite = requestedFavorite,
                    visible = previousVisible
                )
                emitError(visibleResponse)
            }
            updatingRestaurantIds.remove(restaurantId)
        }
    }

    fun updateOrder(
        order: RestaurantOrder,
        previousOrder: RestaurantOrder,
        favoriteOnly: Boolean
    ) {
        updateRestaurantsOrder(order.order)

        viewModelScope.launch {
            when (val response = restaurantRepository.updatePersonalRestaurantOrder(order.order)) {
                is NetworkResult.Success -> {
                    updateRestaurantsOrder(response.body)
                    val confirmedOrder = RestaurantOrder(response.body)
                    restaurantRepository.restaurantsOrder.setValue(confirmedOrder)
                    if (favoriteOnly) {
                        restaurantRepository.favoriteRestaurantsOrder.setValue(confirmedOrder)
                    }
                }
                else -> {
                    updateRestaurantsOrder(previousOrder.order)
                    emitError(response)
                }
            }
        }
    }

    private fun currentRestaurants() =
        (_uiState.value as? ReorderRestaurantUiState.Success)?.restaurants.orEmpty()

    private fun updateRestaurantState(
        restaurantId: Long,
        isFavorite: Boolean,
        visible: Boolean
    ) {
        val restaurants = currentRestaurants()
        if (restaurants.isEmpty()) return
        _uiState.value =
            ReorderRestaurantUiState.Success(
                restaurants.map { restaurant ->
                    if (restaurant.id == restaurantId) {
                        restaurant.copy(isFavorite = isFavorite, visible = visible)
                    } else {
                        restaurant
                    }
                }
            )
    }

    private fun updateRestaurantsOrder(order: List<Long>) {
        val restaurants = currentRestaurants()
        if (restaurants.isEmpty()) return
        val restaurantsById = restaurants.associateBy { it.id }
        val orderedIds = order.toSet()
        val orderedRestaurants = order.mapNotNull(restaurantsById::get)
        val remainingRestaurants = restaurants.filter { it.id !in orderedIds }
        _uiState.value =
            ReorderRestaurantUiState.Success(orderedRestaurants + remainingRestaurants)
    }

    private suspend fun emitError(result: NetworkResult<*>) {
        _errorEvents.emit(
            when (result) {
                is NetworkResult.Failure -> result.message
                is NetworkResult.NetworkError -> NETWORK_ERROR_MESSAGE
                is NetworkResult.UnknownError -> UNKNOWN_ERROR_MESSAGE
                is NetworkResult.Success -> return
            }
        )
    }

    private companion object {
        const val NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다."
        const val UNKNOWN_ERROR_MESSAGE = "알 수 없는 오류가 발생했습니다."
    }
}
