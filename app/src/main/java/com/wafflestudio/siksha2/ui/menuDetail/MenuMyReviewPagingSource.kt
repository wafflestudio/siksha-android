package com.wafflestudio.siksha2.ui.menuDetail

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.ReviewRestaurant
import com.wafflestudio.siksha2.network.result.NetworkResult

class MenuMyReviewPagingSource(
    private val api: SikshaApi
) : PagingSource<Long, ReviewRestaurant>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ReviewRestaurant> {
        val key = params.key ?: STARTING_PAGE_INDEX
        return when (val response = api.fetchMyReviews(key, params.loadSize.toLong())) {
            is NetworkResult.Success -> {
                LoadResult.Page(
                    data = response.body.result,
                    prevKey = if (key == 1L) null else key - 1,
                    nextKey = if (response.body.result.isEmpty()) null else if (key == STARTING_PAGE_INDEX) key + params.loadSize / PAGE_LOAD_SIZE else key + 1
                )
            }
            else -> LoadResult.Error(RuntimeException(""))
        }
    }

    override fun getRefreshKey(state: PagingState<Long, ReviewRestaurant>): Long {
        return STARTING_PAGE_INDEX
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1L
        private const val PAGE_LOAD_SIZE = 7
        val Config = PagingConfig(pageSize = PAGE_LOAD_SIZE)
    }
}
