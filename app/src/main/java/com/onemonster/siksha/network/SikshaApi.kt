package com.onemonster.siksha.network

import com.onemonster.siksha.model.MenuResponse
import retrofit2.Call
import retrofit2.http.GET

interface SikshaApi {
    @GET("api/v1/snu/menus")
    fun fetchMenus(): Call<MenuResponse>
}