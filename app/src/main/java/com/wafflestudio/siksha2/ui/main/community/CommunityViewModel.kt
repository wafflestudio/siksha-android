package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource.Companion.ITEMS_PER_PAGE
import com.wafflestudio.siksha2.utils.Selectable
import com.wafflestudio.siksha2.utils.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _boards = MutableStateFlow<List<Selectable<Board>>>(emptyList())
    val boards: StateFlow<List<Selectable<Board>>> get() = _boards

    private val selectedBoard = _boards.map { list ->
        list.find { it.state }?.data
    }.filterNotNull()

    private val _postPagingData = selectedBoard.map { board ->
        Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { communityRepository.postPagingSource(board.id) }
        ).flow.cachedIn(viewModelScope)
    }
    val postPagingData = _postPagingData.stateIn(viewModelScope, SharingStarted.Eagerly, flowOf(PagingData.empty()))

    init {
        viewModelScope.launch {
            getBoards()
        }
    }

    suspend fun getBoards() {
        try {
            _boards.value = communityRepository.getBoards().map { board ->
                board.toDataWithState(false)
            }
            selectBoard(0)
        } catch (e: IOException) {
            // TODO: error handler
        }
    }

    fun selectBoard(boardIndex: Int) {
        _boards.value = _boards.value.mapIndexed { idx, board ->
            board.data.toDataWithState(idx == boardIndex)
        }
    }
}
