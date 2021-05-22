package com.wafflestudio.siksha2.ui.menu_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.repositories.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {
    private val _menu = MutableLiveData<Menu>()
    val menu: LiveData<Menu>
        get() = _menu

    private val _commentHint = MutableLiveData<String>()
    val commentHint: LiveData<String>
        get() = _commentHint

    private val _networkResultState = MutableLiveData<State>()
    val networkResultState: LiveData<State>
        get() = _networkResultState

    private val _reviewDistribution = MutableLiveData<List<Long>>()
    val reviewDistribution: LiveData<List<Long>>
        get() = _reviewDistribution

    fun refreshMenu(menuId: Long) {
        _networkResultState.value = State.LOADING
        viewModelScope.launch {
            try {
                _menu.value = menuRepository.getMenuById(menuId)
                _networkResultState.value = State.SUCCESS
            } catch (e: Exception) {
                _networkResultState.value = State.FAILED
            }
        }
    }

    fun getReviews(menuId: Long): Flow<PagingData<Review>> {
        return menuRepository.getPagedReviewsByMenuIdFlow(menuId)
    }

    fun getRecommendationReview(score: Long) {
        // TODO: LruCache 로 캐싱해놓고 꺼내쓰기
        viewModelScope.launch {
            _commentHint.value = menuRepository.getReviewRecommendationComments(score)
        }
    }

    fun refreshReviewDistribution(menuId: Long) {
        viewModelScope.launch {
            _reviewDistribution.value = menuRepository.getReviewDistribution(menuId)
        }
    }

    suspend fun leaveReview(score: Double, comment: String) {
        _menu.value?.id?.let { id ->
            menuRepository.leaveMenuReview(id, score, comment)
        }
    }

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }
}
