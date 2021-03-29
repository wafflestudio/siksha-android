package com.wafflestudio.siksha2.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wafflestudio.siksha2.models.DailyMenu
import com.wafflestudio.siksha2.models.RestaurantInfo

@Database(entities = [RestaurantInfo::class, DailyMenu::class], version = 1)
@TypeConverters(SikshaRoomTypeAdapter::class)
abstract class SikshaDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantsDao
    abstract fun dailyMenuDao(): DailyMenusDao
}
