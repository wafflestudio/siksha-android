package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.GetAlarmTypeResponse
import com.wafflestudio.siksha2.network.dto.GetFavoriteMenusResponse
import com.wafflestudio.siksha2.network.result.NetworkResult
import javax.inject.Inject

class FavoriteMenuRepository @Inject constructor(
    private val api: SikshaApi
) {
    suspend fun getFavoriteMenus(token: String): NetworkResult<GetFavoriteMenusResponse> {
        return api.getFavoriteMenus(token)
    }

    suspend fun enableAlarm(token: String, menuId: Long) =
        api.postAlarmOn(menuId, token)

    suspend fun disableAlarm(token: String, menuId: Long) =
        api.postAlarmOff(menuId, token)

    suspend fun enableAllMenuAlarms(token: String) =
        api.postAlarmOnAll(token)

    suspend fun disableAllMenuAlarms(token: String) =
        api.postAlarmOffAll(token)

    suspend fun setAlarmType(type: String, token: String): NetworkResult<Unit> {
        val body = mapOf("type" to type)
        return api.postAlarmType(token, body)
    }

    suspend fun fetchAlarmType(token: String): NetworkResult<GetAlarmTypeResponse> {
        return api.getAlarmType(token)
    }
}
