package com.wafflestudio.siksha2.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "restaurants")
@JsonClass(generateAdapter = true)
data class RestaurantInfo(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    var id: Long = -1,
    @ColumnInfo(name = "code")
    @Json(name = "code")
    var restaurantCode: String = "",
    @ColumnInfo(name = "name_kr")
    @Json(name = "name_kr")
    var nameKr: String? = null,
    @ColumnInfo(name = "name_en")
    @Json(name = "name_en")
    var nameEn: String? = null,
    @ColumnInfo(name = "address")
    @Json(name = "addr")
    var address: String? = null,
    @ColumnInfo(name = "lat")
    @Json(name = "lat")
    var latitude: Double? = null,
    @ColumnInfo(name = "lng")
    @Json(name = "lng")
    var longitude: Double? = null,
    @ColumnInfo(name = "etc")
    @Json(name = "etc")
    var etc: Extra? = null,
    @ColumnInfo(name = "is_favorite")
    @Transient()
    var isFavorite: Boolean = false
) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Extra(
        @Json(name = "operating_hours") val operatingHours: OperatingHour
    ) : Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class OperatingHour(
        val weekdays: List<String>,
        val saturday: List<String>,
        val holiday: List<String>
    ) : Parcelable
}
