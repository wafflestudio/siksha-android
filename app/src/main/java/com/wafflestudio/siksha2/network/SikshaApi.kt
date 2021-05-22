package com.wafflestudio.siksha2.network

import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.network.dto.*
import retrofit2.http.*
import java.time.LocalDate

interface SikshaApi {
    @GET("menus/")
    suspend fun fetchMenuGroups(
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate
    ): FetchMenuGroupsResult

    @GET("menus/{menu_id}")
    suspend fun fetchMenuById(@Path(value = "menu_id") menuId: Long): Menu

    @GET("reviews/")
    suspend fun fetchReviews(
        @Query("menu_id") menuId: Long,
        @Query("page") page: Long,
        @Query("perPage") perPage: Long
    ): FetchReviewsResult

    @GET("restaurants/")
    suspend fun fetchRestaurants(): FetchRestaurantsResult

    @POST("reviews/")
    suspend fun leaveMenuReview(@Body req: LeaveReviewParam): LeaveReviewResult

    @POST("auth/login/kakao")
    suspend fun loginKakao(@Header("kakao-token") kakaoToken: String): LoginOAuthResult

    @POST("auth/login/google")
    suspend fun loginGoogle(@Header("google-token") googleToken: String): LoginOAuthResult

    @DELETE("auth/")
    suspend fun deleteAccount()

    @POST("auth/refresh")
    suspend fun refreshToken(@Header("authorization-token") token: String): LoginOAuthResult

    @GET("reviews/comments/recommendation")
    suspend fun fetchRecommendationReviewComments(@Query("score") score: Long):
        FetchRecommendationReviewCommentsResult

    @GET("reviews/dist")
    suspend fun fetchReviewDistribution(@Query("menu_id") menuId: Long):
        FetchReviewDistributionResult
}
