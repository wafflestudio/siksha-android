package com.wafflestudio.siksha.network

import com.wafflestudio.siksha.model.MenuResponse
import retrofit2.Call
import retrofit2.http.GET

interface SikshaApi {
    @GET("api/v1/snu/menus")
    fun fetchMenus(): Call<MenuResponse>
}