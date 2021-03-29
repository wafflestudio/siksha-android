package com.wafflestudio.siksha2.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.network.dto.FetchMenuGroupsResult
import java.time.LocalDate

@Entity(tableName = "daily_menus")
@JsonClass(generateAdapter = true)
data class DailyMenu(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "data")
    val data: FetchMenuGroupsResult.DailyMenuGroupResponse
)
