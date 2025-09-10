package com.wafflestudio.siksha2.network

import com.wafflestudio.siksha2.network.dto.FestivalDates
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.network.dto.*
import com.wafflestudio.siksha2.network.result.NetworkResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface SikshaApi {
    @GET("/menus/lo")
    suspend fun fetchMenuGroups(
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate
    ): NetworkResult<FetchMenuGroupsResult>

    @GET("/menus/{menu_id}")
    suspend fun fetchMenuById(@Path(value = "menu_id") menuId: Long): NetworkResult<Menu>

    @GET("/menus/festival/dates")
    suspend fun fetchFestivalDates(): NetworkResult<FestivalDates>

    @GET("/menus/festival")
    suspend fun isFestivalDate(@Path(value = "input_date") inputDate: String): NetworkResult<FestivalDateCheckResponse>

    @GET("/reviews/")
    suspend fun fetchReviews(
        @Query("menu_id") menuId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Long
    ): NetworkResult<FetchReviewsResult>

    @GET("/reviews/filter")
    suspend fun fetchReviewsWithImage(
        @Query("menu_id") menuId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Long,
        @Query("etc") etc: Boolean = true
    ): NetworkResult<FetchReviewsResult>

    @GET("/restaurants/")
    suspend fun fetchRestaurants(): NetworkResult<FetchRestaurantsResult>

    @GET("/menus/me")
    suspend fun getFavoriteMenus(
        @Header("authorization-token") token: String
    ): NetworkResult<GetFavoriteMenusResponse>

    @POST("/reviews/")
    suspend fun leaveMenuReview(@Body req: LeaveReviewParam): NetworkResult<LeaveReviewResult>

    @Multipart
    @POST("/reviews/images")
    suspend fun leaveMenuReviewImages(
        @Part("menu_id") menuId: Long,
        @Part("score") score: Long,
        @Part comment: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    ): NetworkResult<LeaveReviewResult>

    @POST("/auth/login/kakao")
    suspend fun loginKakao(@Header("kakao-token") kakaoToken: String): NetworkResult<LoginOAuthResult>

    @POST("/auth/login/google")
    suspend fun loginGoogle(@Header("google-token") googleToken: String): NetworkResult<LoginOAuthResult>

    @DELETE("/auth/")
    suspend fun deleteAccount()

    @POST("/auth/refresh")
    suspend fun refreshToken(@Header("authorization-token") token: String): NetworkResult<LoginOAuthResult>

    @GET("/reviews/comments/recommendation")
    suspend fun fetchRecommendationReviewComments(@Query("score") score: Long):
        NetworkResult<FetchRecommendationReviewCommentsResult>

    @GET("/reviews/dist")
    suspend fun fetchReviewDistribution(@Query("menu_id") menuId: Long):
        NetworkResult<FetchReviewDistributionResult>

    @POST("/voc")
    suspend fun sendVoc(
        @Body req: VocParam
    ): NetworkResult<Unit>

    @GET("/auth/me/image")
    suspend fun getUserData(): NetworkResult<GetUserDataResult>

    @Multipart
    @PATCH("/auth/me/image/profile")
    suspend fun updateUserData(
        @Part image: MultipartBody.Part?,
        @Part("change_to_default_image") changeToDefaultImage: Boolean,
        @Part nickname: MultipartBody.Part?
    ): NetworkResult<GetUserDataResult>

    @GET("/auth/nicknames/validate")
    suspend fun checkNickname(
        @Query("nickname") nickname: String
    ): NetworkResult<Unit>

    @GET("/versions/android")
    suspend fun getVersion(): NetworkResult<GetVersionResult>

    @POST("/menus/{menu_id}/like")
    suspend fun postLikeMenu(@Path("menu_id") menuId: Long): NetworkResult<MenuLikeOrUnlikeResponse>

    @POST("/menus/{menu_id}/unlike")
    suspend fun postUnlikeMenu(@Path("menu_id") menuId: Long): NetworkResult<MenuLikeOrUnlikeResponse>

    @GET("/community/boards")
    suspend fun getBoards(): NetworkResult<GetBoardsResult>

    @GET("/community/boards/{board_id}")
    suspend fun getBoard(
        @Path("board_id") boardId: Long
    ): NetworkResult<GetBoardResult>

    @GET("/community/posts")
    suspend fun getPosts(
        @Query("board_id") boardId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Int
    ): NetworkResult<GetPostsResult>

    @GET("/community/posts/me")
    suspend fun getUserPosts(
        @Query("page") page: Long,
        @Query("per_page") perPage: Int
    ): NetworkResult<GetPostsResult>

    @GET("/community/posts/{post_id}")
    suspend fun getPost(
        @Path("post_id") postId: Long
    ): NetworkResult<GetPostResult>

    @GET("/community/comments")
    suspend fun getComments(
        @Query("post_id") postId: Long,
        @Query("page") page: Long,
        @Query("per_page") perPage: Int
    ): NetworkResult<GetCommentsResult>

    @POST("/community/comments")
    suspend fun postComment(
        @Body body: PostCommentRequestBody
    ): NetworkResult<PostCommentResponse>

    @POST("/community/posts/{post_id}/like")
    suspend fun postLikePost(
        @Path("post_id") postId: Long
    ): NetworkResult<PostLikePostResponse>

    @POST("/community/posts/{post_id}/unlike")
    suspend fun postUnlikePost(
        @Path("post_id") postId: Long
    ): NetworkResult<PostUnlikePostResponse>

    @Multipart
    @POST("/community/posts")
    suspend fun postCreatePost(
        @Part("board_id") boardId: Long,
        @Part title: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part("anonymous") anonymous: Boolean,
        @Part images: List<MultipartBody.Part>
    ): NetworkResult<CreatePostResponse>

    @Multipart
    @PATCH("/community/posts/{post_id}")
    suspend fun postPatchPost(
        @Path("post_id") postId: Long,
        @Part("board_id") boardId: Long,
        @Part title: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part("anonymous") anonymous: Boolean,
        @Part images: List<MultipartBody.Part>
    ): NetworkResult<PatchPostResponse>

    @POST("/community/comments/{comment_id}/like")
    suspend fun postLikeComment(
        @Path("comment_id") commentId: Long
    ): NetworkResult<PostLikeCommentResponse>

    @POST("/community/comments/{comment_id}/unlike")
    suspend fun postUnlikeComment(
        @Path("comment_id") commentId: Long
    ): NetworkResult<PostUnlikeCommentResponse>

    @DELETE("community/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long
    ): Response<Unit?>

    @DELETE("/community/comments/{comment_id}")
    suspend fun deleteComment(
        @Path("comment_id") commentId: Long
    ): Response<Unit?>

    @POST("/community/posts/{post_id}/report")
    suspend fun reportPost(
        @Path("post_id") postId: Long,
        @Body requestBody: ReportPostRequestBody
    ): NetworkResult<ReportPostResponse>

    @POST("/community/comments/{comment_id}/report")
    suspend fun reportComment(
        @Path("comment_id") commentId: Long,
        @Body requestBody: ReportCommentRequestBody
    ): NetworkResult<ReportCommentResponse>

    @GET("/community/posts/popular/trending")
    suspend fun getTrendingPosts(): NetworkResult<GetTrendingPostsResponse>
}
