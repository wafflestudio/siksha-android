package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.db.RestaurantsDao
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.PersonalRestaurantDto
import com.wafflestudio.siksha2.network.dto.RestaurantLikeRequest
import com.wafflestudio.siksha2.network.dto.RestaurantLikeResponse
import com.wafflestudio.siksha2.network.dto.RestaurantOrder
import com.wafflestudio.siksha2.network.dto.RestaurantVisibleRequest
import com.wafflestudio.siksha2.network.dto.RestaurantVisibleResponse
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    sikshaPrefObjects: SikshaPrefObjects,
    private val sikshaApi: SikshaApi,
    private val restaurantsDao: RestaurantsDao
) {
    val showEmptyRestaurant = sikshaPrefObjects.showEmptyRestaurant
    val restaurantsOrder = sikshaPrefObjects.restaurantsOrder
    val favoriteRestaurantsOrder = sikshaPrefObjects.favoriteRestaurantsOrder

    suspend fun syncWithServer() {
        withContext(Dispatchers.IO) {
            when (val response = sikshaApi.fetchRestaurants()) {
                is NetworkResult.Success -> {
                    val data = response.body
                    restaurantsDao.update(data.result)
                }
                else -> {
                    throw IOException("")
                }
            }
        }
    }

    suspend fun fetchPersonalRestaurants(): NetworkResult<List<RestaurantInfo>> =
        withContext(Dispatchers.IO) {
            when (val response = sikshaApi.fetchPersonalRestaurants()) {
                is NetworkResult.Success -> {
                    val restaurants =
                        response.body.result.map(PersonalRestaurantDto::toRestaurantInfo)
                    restaurantsDao.insert(restaurants)
                    NetworkResult.Success(restaurants)
                }
                is NetworkResult.Failure -> response
                is NetworkResult.NetworkError -> response
                is NetworkResult.UnknownError -> response
            }
        }

    suspend fun setPersonalRestaurantFavoriteById(
        id: Long,
        isFavorite: Boolean
    ): NetworkResult<RestaurantLikeResponse> =
        withContext(Dispatchers.IO) {
            val response = sikshaApi.setRestaurantFavorite(
                restaurantId = id,
                body = RestaurantLikeRequest(like = isFavorite)
            )
            if (response is NetworkResult.Success) {
                restaurantsDao.setRestaurantFavoriteById(id, response.body.liked)
            }
            response
        }

    suspend fun setPersonalRestaurantVisibleById(
        id: Long,
        visible: Boolean
    ): NetworkResult<RestaurantVisibleResponse> =
        withContext(Dispatchers.IO) {
            val response = sikshaApi.setRestaurantVisible(
                restaurantId = id,
                body = RestaurantVisibleRequest(visible = visible)
            )
            if (response is NetworkResult.Success) {
                restaurantsDao.setRestaurantVisibleById(id, response.body.visible)
            }
            response
        }

    suspend fun updatePersonalRestaurantOrder(
        order: List<Long>
    ): NetworkResult<List<Long>> =
        withContext(Dispatchers.IO) {
            sikshaApi.updateRestaurantOrder(RestaurantOrder(order)).map { response ->
                response.order
            }
        }

    fun getAllRestaurantsFlow(): Flow<List<RestaurantInfo>> {
        return restaurantsDao.getAllFlow()
    }

    suspend fun getRestaurantById(restaurantId: Long): RestaurantInfo? {
        return restaurantsDao.getRestaurantById(restaurantId)
    }

    suspend fun toggleRestaurantFavoriteById(id: Long) {
        restaurantsDao.toggleRestaurantFavoriteById(id)
    }

    suspend fun getOrderedRestaurants(): List<RestaurantInfo> {
        val allRestaurants = restaurantsDao.getAll()
        val order = restaurantsOrder.getValue().order
        return alignWithOrder(allRestaurants, order)
    }

    suspend fun getOrderedFavoriteRestaurants(): List<RestaurantInfo> {
        val favoriteRestaurant = restaurantsDao.getFavoriteAll()
        val order = favoriteRestaurantsOrder.getValue().order
        return alignWithOrder(favoriteRestaurant, order)
    }

    private fun alignWithOrder(
        list: List<RestaurantInfo>,
        order: List<Long>
    ): List<RestaurantInfo> {
        val sortedList = list.sortedByDescending { it.id }
        val result = mutableListOf<RestaurantInfo>()
        order.forEach { id ->
            sortedList.find { it.id == id }?.let { result.add(it) }
        }
        result.addAll(sortedList.filter { (it.id in order).not() })
        return result
    }
}
