package com.wafflestudio.siksha2.network

import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.network.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*
import java.time.LocalDate

interface SikshaApi {
    @GET("/menus/lo")
    suspend fun fetchMenuGroups(
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate
    ): FetchMenuGroupsResult

    @GET("/menus/{menu_id}")
    suspend fun fetchMenuById(@Path(value = "menu_id") menuId: Long): Menu

    @GET("/reviews/")
    suspend fun fetchReviews(
        @Query("menu_id") menuId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Long
    ): FetchReviewsResult

    @GET("/reviews/filter")
    suspend fun fetchReviewsWithImage(
        @Query("menu_id") menuId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Long,
        @Query("etc") etc: Boolean = true
    ): FetchReviewsResult

    @GET("/restaurants/")
    suspend fun fetchRestaurants(): FetchRestaurantsResult

    @POST("/reviews/")
    suspend fun leaveMenuReview(@Body req: LeaveReviewParam): LeaveReviewResult

    @Multipart
    @POST("/reviews/images")
    suspend fun leaveMenuReviewImages(
        @Part("menu_id") menuId: Long,
        @Part("score") score: Long,
        @Part comment: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    ): LeaveReviewResult

    @POST("/auth/login/kakao")
    suspend fun loginKakao(@Header("kakao-token") kakaoToken: String): LoginOAuthResult

    @POST("/auth/login/google")
    suspend fun loginGoogle(@Header("google-token") googleToken: String): LoginOAuthResult

    @DELETE("/auth/")
    suspend fun deleteAccount()

    @POST("/auth/refresh")
    suspend fun refreshToken(@Header("authorization-token") token: String): LoginOAuthResult

    @GET("/reviews/comments/recommendation")
    suspend fun fetchRecommendationReviewComments(@Query("score") score: Long):
        FetchRecommendationReviewCommentsResult

    @GET("/reviews/dist")
    suspend fun fetchReviewDistribution(@Query("menu_id") menuId: Long):
        FetchReviewDistributionResult

    @POST("/voc")
    suspend fun sendVoc(
        @Body req: VocParam
    )

    @GET("/auth/me")
    suspend fun getUserData(): GetUserDataResult

    @GET("/versions/android")
    suspend fun getVersion(): GetVersionResult

    @POST("/menus/{menu_id}/like")
    suspend fun likeMenu(@Path("menu_id") menuId: Long): MenuLikeOrUnlikeResponse

    @POST("/menus/{menu_id}/unlike")
    suspend fun unlikeMenu(@Path("menu_id") menuId: Long): MenuLikeOrUnlikeResponse
}
