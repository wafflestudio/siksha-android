package com.wafflestudio.siksha2.db

import androidx.room.*
import com.wafflestudio.siksha2.models.RestaurantInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantsDao {
    @Query("SELECT * FROM restaurants")
    suspend fun getAll(): List<RestaurantInfo>

    @Query("SELECT * FROM restaurants")
    fun getAllFlow(): Flow<List<RestaurantInfo>>

    @Query("SELECT * FROM restaurants WHERE id=:id")
    suspend fun getRestaurantById(id: Long): RestaurantInfo?

    @Query("SELECT * FROM restaurants WHERE is_favorite")
    suspend fun getFavoriteAll(): List<RestaurantInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: List<RestaurantInfo>)

    @Transaction
    suspend fun update(item: List<RestaurantInfo>) {
        val favoritesId = getFavoriteAll().map { it.id }
        val result = item.map { it.copy(isFavorite = it.id in favoritesId) }
        insert(result)
    }

    @Transaction
    suspend fun toggleRestaurantFavoriteById(id: Long) {
        val target = getRestaurantById(id)
        target?.let {
            val temp = it.copy(isFavorite = it.isFavorite.not())
            insert(listOf(temp))
        }
    }
}
