package com.wafflestudio.siksha2.ui.menuDetail

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.siksha2.models.KeywordDist
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.dto.LeaveReviewResult
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.utils.ImageUtil
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
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

    private val _keywordDistribution = MutableLiveData<KeywordDist>()
    val keywordDistribution: LiveData<KeywordDist>
        get() = _keywordDistribution

    private val _selectedKeywordList = MutableStateFlow<List<String>>(listOf("", "", ""))
    val selectedKeywordList: StateFlow<List<String>>
        get() = _selectedKeywordList

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    private val _imageUrlList = MutableLiveData<List<String>>()
    val imageUrlList: LiveData<List<String>>
        get() = _imageUrlList

    private val _imageCount = MutableLiveData<Long>(0)
    val imageCount: LiveData<Long>
        get() = _imageCount

    private val _leaveReviewState = MutableLiveData<ReviewState>(ReviewState.WAITING)
    val leaveReviewState: LiveData<ReviewState>
        get() = _leaveReviewState

    private val _reviewRating = mutableFloatStateOf(5f)
    val reviewRating: FloatState
        get() = _reviewRating

    fun refreshMenu(menuId: Long) {
        _networkResultState.value = State.LOADING
        viewModelScope.launch {
            val result = menuRepository.getMenuById(menuId)
            when (result) {
                is NetworkResult.Success -> {
                    _menu.value = result.body
                    _networkResultState.value = State.SUCCESS
                }
                else -> _networkResultState.value = State.FAILED
            }
        }
    }

    fun refreshImages(menuId: Long) {
        viewModelScope.launch {
            when (val response = menuRepository.getFirstReviewPhotoByMenuId(menuId)) {
                is NetworkResult.Success -> {
                    val data = response.body
                    _imageCount.value = data.totalCount
                    val urlList = emptyList<String>().toMutableList()
                    for (i in 0 until 3) {
                        if (i < data.result.size) {
                            data.result[i].etc?.images?.get(0)?.let {
                                urlList.add(it)
                            }
                        }
                    }
                    _imageUrlList.value = urlList
                }
                else -> {
                    _imageUrlList.value = emptyList()
                    _networkResultState.value = State.FAILED
                }
            }
        }
    }

    fun getReviews(menuId: Long): Flow<PagingData<Review>> {
        return menuRepository.getPagedReviewsByMenuIdFlow(menuId)
    }

    fun getReviewsWithImages(menuId: Long): Flow<PagingData<Review>> {
        return menuRepository.getPagedReviewsOnlyHaveImagesByMenuIdFlow(menuId)
    }

    fun getRecommendationReview(score: Long) {
        // TODO: LruCache 로 캐싱해놓고 꺼내쓰기
        viewModelScope.launch {
            when (val response = menuRepository.getReviewRecommendationComments(score)) {
                is NetworkResult.Success -> {
                    _commentHint.value = response.body.comment
                }
                else -> _commentHint.value = ""
            }
        }
    }

    fun setReviewRating(newRating: Float) {
        _reviewRating.floatValue = newRating
    }

    fun refreshReviewDistribution(menuId: Long) {
        viewModelScope.launch {
            when (val response = menuRepository.getReviewDistribution(menuId)) {
                is NetworkResult.Success -> _reviewDistribution.value = response.body.dist
                else -> _reviewDistribution.value = emptyList()
            }
        }
    }

    fun refreshKeywordDistribution(menuId: Long) {
        viewModelScope.launch {
            when (val response = menuRepository.getKeywordDist(menuId)) {
                is NetworkResult.Success -> _keywordDistribution.value = response.body
                else -> _keywordDistribution.value = KeywordDist(listOf(), listOf())
            }
        }
    }

    fun selectKeyword(idx: Int, keyword: String) {
        _selectedKeywordList.value = _selectedKeywordList.value.toMutableList().also { it[idx] = keyword }
    }

    fun addImageUri(uri: Uri, onFailure: () -> Unit) {
        val list = _imageUriList.value?.toMutableList() ?: mutableListOf()
        if (list.size < 3) {
            list.add(uri)
            _imageUriList.value = list.toList()
        } else {
            onFailure()
        }
    }

    fun deleteImageUri(index: Int, onFailure: () -> Unit = {}) {
        val list = _imageUriList.value?.toMutableList() ?: mutableListOf()
        if (index < list.size) {
            list.removeAt(index)
            _imageUriList.value = list.toList()
        } else {
            onFailure()
        }
    }

    fun refreshUriList() {
        _imageUriList.value = listOf()
    }

    fun notifySendReviewEnd() {
        _leaveReviewState.value = ReviewState.WAITING
    }

    suspend fun toggleLike(id: Long, isCurrentlyLiked: Boolean): NetworkResult<Menu> {
        val menuUpdateResponse = when (isCurrentlyLiked) {
            true -> menuRepository.unlikeMenuById(id)
            false -> menuRepository.likeMenuById(id)
        }
        when (menuUpdateResponse) {
            is NetworkResult.Success -> {
                _menu.postValue(menuUpdateResponse.body)
            }
            else -> { }
        }
        return menuUpdateResponse
    }

    suspend fun leaveReview(context: Context, score: Double, comment: String): NetworkResult<LeaveReviewResult>? {
        Timber.d("LeaveReview ${_menu.value?.id}")
        val menuId = _menu.value?.id ?: return null
        Timber.d("not null")
        val response = if (_imageUriList.value?.isNotEmpty() == true) {
            context.showToast("이미지 압축 중입니다.")
            _leaveReviewState.value = ReviewState.COMPRESSING
            val imageList = _imageUriList.value?.map {
                ImageUtil.getCompressedImage(context, it)
            }?.map { file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestBody)
            }
            val commentBody = MultipartBody.Part.createFormData("comment", comment)
            imageList?.let {
                menuRepository.leaveMenuReviewImage(
                    menuId,
                    score.toLong(),
                    "",
                    "",
                    "",
                    commentBody,
                    imageList
                )
            }
        } else {
            menuRepository.leaveMenuReview(
                menuId,
                score,
                "",
                "",
                "",
                comment
            )
        }
        return response
    }

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }

    enum class ReviewState {
        WAITING,
        COMPRESSING
    }
}
