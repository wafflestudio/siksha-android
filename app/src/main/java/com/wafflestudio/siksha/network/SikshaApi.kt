package com.wafflestudio.siksha.network

import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.model.Review
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SikshaApi {
    @GET("api/v1/snu/menus/")
    fun fetchMenus(): Call<MenuResponse>

    @POST("api/v1/snu/review/")
    fun leaveReview(@Body encoded: String): Call<Review>
}