package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.db.RestaurantsDao
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
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
            val data = sikshaApi.fetchRestaurants()
            restaurantsDao.update(data.result)
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
        val result = mutableListOf<RestaurantInfo>()
        order.forEach { id ->
            list.find { it.id == id }?.let { result.add(it) }
        }
        result.addAll(list.filter { (it.id in order).not() })
        return result
    }
}
