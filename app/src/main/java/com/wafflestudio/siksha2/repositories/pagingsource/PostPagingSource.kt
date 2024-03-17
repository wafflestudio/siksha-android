package com.wafflestudio.siksha2.repositories.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.network.SikshaApi
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(
    val boardId: Long,
    private val api: SikshaApi
) : PagingSource<Long, Post>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        val page = params.key ?: STARTING_KEY
        return try {
            val response = api.getPosts(
                boardId = boardId,
                page = page,
                perPage = params.loadSize
            )

            LoadResult.Page(
                data = response.result.map { it.toPost() },
                prevKey = when (page) {
                    STARTING_KEY -> null
                    else -> page - 1
                },
                nextKey = if (response.hasNext) page + params.loadSize / ITEMS_PER_PAGE else null
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
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
