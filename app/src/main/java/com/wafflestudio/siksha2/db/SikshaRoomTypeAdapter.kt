package com.wafflestudio.siksha2.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.network.dto.FetchMenuGroupsResult
import java.time.LocalDate

@ProvidedTypeConverter
class SikshaRoomTypeAdapter(private val moshi: Moshi) {
    @TypeConverter
    fun fromExtra(value: RestaurantInfo.Extra?): String? {
        return moshi.adapter(RestaurantInfo.Extra::class.java).toJson(value)
    }

    @TypeConverter
    fun toExtra(value: String?): RestaurantInfo.Extra? {
        if (value == null) return null
        return moshi.adapter(RestaurantInfo.Extra::class.java).fromJson(value)
    }

    @TypeConverter
    fun fromResult(value: FetchMenuGroupsResult.DailyMenuGroupResponse): String {
        return moshi.adapter(FetchMenuGroupsResult.DailyMenuGroupResponse::class.java).toJson(value)
    }

    @TypeConverter
    fun toResult(value: String): FetchMenuGroupsResult.DailyMenuGroupResponse {
        // FIXME: !!
        return moshi.adapter(FetchMenuGroupsResult.DailyMenuGroupResponse::class.java)
            .fromJson(value)!!
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate): String {
        return moshi.adapter(LocalDate::class.java).toJson(value)
    }

    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return moshi.adapter(LocalDate::class.java).fromJson(value)!!
    }
}
