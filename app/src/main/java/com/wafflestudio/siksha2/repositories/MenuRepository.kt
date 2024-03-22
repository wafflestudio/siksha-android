package com.wafflestudio.siksha2.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import com.wafflestudio.siksha2.db.DailyMenusDao
import com.wafflestudio.siksha2.models.DailyMenu
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.FetchReviewsResult
import com.wafflestudio.siksha2.network.dto.LeaveReviewParam
import com.wafflestudio.siksha2.network.dto.LeaveReviewResult
import com.wafflestudio.siksha2.ui.menuDetail.MenuReviewPagingSource
import com.wafflestudio.siksha2.ui.menuDetail.MenuReviewWithImagePagingSource
import com.wafflestudio.siksha2.utils.toLocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val sikshaApi: SikshaApi,
    private val dailyMenusDao: DailyMenusDao
) {

    // Client Heuristic: 앞 뒤 1일 정도를 캐싱해둔다.
    suspend fun syncWithServer(date: LocalDate) {
        withContext(Dispatchers.IO) {
            val startDate = date.minusDays(1)
            val endDate = date.plusDays(1)
            val payload = sikshaApi.fetchMenuGroups(startDate, endDate).result
                .map {
                    DailyMenu(it.date.toLocalDate(), it)
                }
            dailyMenusDao.insertDailyMenus(payload)
        }
    }

    // Client Heuristic: 일정 기간 이상으로 오래된 메뉴 데이터는 삭제한다.
    suspend fun sweepOldMenus() {
        withContext(Dispatchers.IO) {
            dailyMenusDao.deleteMenusBefore(LocalDate.now().minusDays(CACHING_DATE_LIMITATION_DAYS))
        }
    }

    fun getDailyMenuFlow(date: LocalDate): Flow<DailyMenu?> {
        return dailyMenusDao.getDailyMenuByDate(date)
    }

    suspend fun getMenuById(menuId: Long): Menu {
        return sikshaApi.fetchMenuById(menuId)
    }

    fun getPagedReviewsByMenuIdFlow(menuId: Long): Flow<PagingData<Review>> {
        return Pager(
            config = MenuReviewPagingSource.Config,
            pagingSourceFactory = { MenuReviewPagingSource(sikshaApi, menuId) }
        ).flow
    }

    fun getPagedReviewsOnlyHaveImagesByMenuIdFlow(menuId: Long): Flow<PagingData<Review>> {
        return Pager(
            config = MenuReviewWithImagePagingSource.Config,
            pagingSourceFactory = { MenuReviewWithImagePagingSource(sikshaApi, menuId) }
        ).flow
    }

    suspend fun leaveMenuReview(menuId: Long, score: Double, comment: String): LeaveReviewResult {
        return sikshaApi.leaveMenuReview(LeaveReviewParam(menuId, score, comment))
    }

    suspend fun leaveMenuReviewImage(menuId: Long, score: Long, comment: MultipartBody.Part, images: List<MultipartBody.Part>): LeaveReviewResult {
        return sikshaApi.leaveMenuReviewImages(menuId, score, comment, images)
    }

    suspend fun getReviewRecommendationComments(score: Long): String {
        return sikshaApi.fetchRecommendationReviewComments(score).comment
    }

    suspend fun getReviewDistribution(menuId: Long): List<Long> {
        return sikshaApi.fetchReviewDistribution(menuId).dist
    }

    suspend fun getFirstReviewPhotoByMenuId(menuId: Long): FetchReviewsResult {
        return sikshaApi.fetchReviewsWithImage(menuId, 1L, 5)
    }

    suspend fun likeMenuById(menuId: Long): Menu {
        return withContext(Dispatchers.IO) {
            val menu = sikshaApi.postLikeMenu(menuId)
            updateMenuInLocal(menu)
            return@withContext menu
        }
    }

    suspend fun unlikeMenuById(menuId: Long): Menu {
        return withContext(Dispatchers.IO) {
            val menu = sikshaApi.postUnlikeMenu(menuId)
            updateMenuInLocal(menu)
            return@withContext menu
        }
    }

    private suspend fun updateMenuInLocal(menu: Menu) { // DB에 저장된 메뉴 중 같은 code인 메뉴들에 좋아요를 반영한다.
        if (menu.date != null) {
            val dailyMenus = dailyMenusDao.getDailyMenuAll()
            val updatedDailyMenus = withContext(Dispatchers.Default) { // TODO: DB 구조, UX 개선
                dailyMenus.map { dailyMenu ->
                    dailyMenu.copy(
                        data = dailyMenu.data.copy(
                            breakfast = dailyMenu.data.breakfast.toUpdatedMenuGroups(menu),
                            lunch = dailyMenu.data.lunch.toUpdatedMenuGroups(menu),
                            dinner = dailyMenu.data.dinner.toUpdatedMenuGroups(menu)
                        )
                    )
                }
            }
            dailyMenusDao.insertDailyMenus(updatedDailyMenus)
        }
    }

    private fun List<MenuGroup>.toUpdatedMenuGroups(menu: Menu): List<MenuGroup> {
        return this.map { menuGroup ->
            if (menuGroup.menus.find { it.code == menu.code } != null) {
                menuGroup.copy(
                    menus = menuGroup.menus.map {
                        if (it.code == menu.code) {
                            it.copy(isLiked = menu.isLiked)
                        } else {
                            it
                        }
                    }
                )
            } else {
                menuGroup
            }
        }
    }

    companion object {
        const val CACHING_DATE_LIMITATION_DAYS = 30L
    }
}
