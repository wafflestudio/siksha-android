package com.wafflestudio.siksha2.repositories.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Comment
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.result.NetworkResult

class CommentPagingSource(
    val postId: Long,
    private val api: SikshaApi
) : PagingSource<Long, Comment>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Comment> {
        val page = params.key ?: STARTING_KEY
        return when (val response = api.getComments(postId, page, params.loadSize)) {
            is NetworkResult.Success -> {
                LoadResult.Page(
                    data = response.body.result.map { it.toComment() },
                    prevKey = if (page == STARTING_KEY) null else page - 1,
                    nextKey = if (response.body.hasNext) page + (params.loadSize / ITEMS_PER_PAGE) else null
                )
            }
            else -> LoadResult.Error(RuntimeException(""))
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Comment>): Long? {
        return null
    }

    companion object {
        const val STARTING_KEY = 1L
        const val ITEMS_PER_PAGE = 10
    }
}
