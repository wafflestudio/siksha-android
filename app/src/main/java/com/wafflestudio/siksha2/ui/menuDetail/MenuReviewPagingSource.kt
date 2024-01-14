package com.wafflestudio.siksha2.ui.menuDetail

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.SikshaApi
import okio.IOException
import retrofit2.HttpException

class MenuReviewPagingSource(
    private val api: SikshaApi,
    private val menuId: Long
) : PagingSource<Long, Review>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Review> {
        val key = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = api.fetchReviews(menuId, key, params.loadSize.toLong())
            val prevKey = if (key == 1L) null else key - 1
            val nextKey = if (response.result.isEmpty()) null else if (key == STARTING_PAGE_INDEX) key + params.loadSize / PAGE_LOAD_SIZE else key + 1

            LoadResult.Page(
                response.result,
                prevKey,
                nextKey
            )
        } catch (e: HttpException) {
            // TODO: 마지막 페이지 일 때 다음페이지 항상 404 뜨면서 페이징이 종료됨
            // 기능상 문제는 없지만 api에서 다음 페이지 유무 확인할 수 있도록 변경 요청하기
            return LoadResult.Error(e)
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Review>): Long {
        return STARTING_PAGE_INDEX
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1L
        private const val PAGE_LOAD_SIZE = 7
        val Config = PagingConfig(pageSize = PAGE_LOAD_SIZE)
    }
}
