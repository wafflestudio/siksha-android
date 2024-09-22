package com.wafflestudio.siksha2.repositories.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.network.SikshaApi
import retrofit2.HttpException
import java.io.IOException

class UserPostPagingSource(
    private val api: SikshaApi
) : PagingSource<Long, Post>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        val page = params.key ?: PostPagingSource.STARTING_KEY
        return try {
            val response = api.getUserPosts(
                page = page,
                perPage = params.loadSize
            )

            LoadResult.Page(
                data = response.result.map { it.toPost() },
                prevKey = when (page) {
                    PostPagingSource.STARTING_KEY -> null
                    else -> page - 1
                },
                nextKey = if (response.hasNext) page + params.loadSize / PostPagingSource.ITEMS_PER_PAGE else null
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
    }
}
