package com.wafflestudio.siksha2.repositories.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.result.NetworkResult

class PostPagingSource(
    val boardId: Long,
    private val api: SikshaApi
) : PagingSource<Long, Post>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        val page = params.key ?: STARTING_KEY
        return when (val response = api.getPosts(boardId, page, params.loadSize)) {
            is NetworkResult.Success -> {
                LoadResult.Page(
                    data = response.body.result.map { it.toPost() },
                    prevKey = if (page == STARTING_KEY) null else page - 1,
                    nextKey = if (response.body.hasNext) page + (params.loadSize / ITEMS_PER_PAGE) else null
                )
            }
            else -> LoadResult.Error(RuntimeException(""))
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Post>): Long {
        return STARTING_KEY
    }

    companion object {
        const val STARTING_KEY = 1L
        const val ITEMS_PER_PAGE = 10
    }
}
