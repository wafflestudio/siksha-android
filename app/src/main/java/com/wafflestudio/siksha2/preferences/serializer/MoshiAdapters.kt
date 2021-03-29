package com.wafflestudio.siksha2.preferences.serializer

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.wafflestudio.siksha2.utils.toISODateString
import com.wafflestudio.siksha2.utils.toLocalDate
import java.time.LocalDate

class MoshiAdapters {
    @ToJson
    fun toJson(localDate: LocalDate): String {
        return localDate.toISODateString()
    }

    @FromJson
    fun fromJson(raw: String): LocalDate {
        return raw.toLocalDate()
    }
}
