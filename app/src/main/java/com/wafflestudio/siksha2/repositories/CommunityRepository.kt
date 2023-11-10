package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val api: SikshaApi
) {
    suspend fun getBoards(): List<Board> {
        return api.getBoards()
    }

    fun postPagingSource(boardId: Long) = PostPagingSource(boardId, api)
}
