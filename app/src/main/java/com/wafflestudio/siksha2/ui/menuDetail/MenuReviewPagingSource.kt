package com.wafflestudio.siksha2.ui.menuDetail

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.result.NetworkResult

class MenuReviewPagingSource(
    private val api: SikshaApi,
    private val menuId: Long
) : PagingSource<Long, Review>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Review> {
        val key = params.key ?: STARTING_PAGE_INDEX
        return when (val response = api.fetchReviews(menuId, key, params.loadSize.toLong())) {
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

    override fun getRefreshKey(state: PagingState<Long, Review>): Long {
        return STARTING_PAGE_INDEX
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1L
        private const val PAGE_LOAD_SIZE = 7
        val Config = PagingConfig(pageSize = PAGE_LOAD_SIZE)
    }
}
