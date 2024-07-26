package com.wafflestudio.siksha2.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wafflestudio.siksha2.models.DailyMenu
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyMenusDao {

    @Query("SELECT * FROM daily_menus WHERE date=:date")
    fun getDailyMenuByDate(date: LocalDate): Flow<DailyMenu?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyMenus(list: List<DailyMenu>)

    @Query("DELETE FROM daily_menus WHERE date<:date")
    suspend fun deleteMenusBefore(date: LocalDate)

    @Query("SELECT * FROM daily_menus")
    suspend fun getDailyMenuAll(): List<DailyMenu>
}
