package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.GetFavoriteMenusResponse
import com.wafflestudio.siksha2.network.result.NetworkResult
import javax.inject.Inject

class FavoriteMenuRepository @Inject constructor(
    private val api: SikshaApi
) {
    suspend fun getFavoriteMenus(token: String): NetworkResult<GetFavoriteMenusResponse> {
        return api.getFavoriteMenus(token)
    }
}


