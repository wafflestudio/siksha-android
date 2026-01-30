package com.wafflestudio.siksha2.ui.menuDetail

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.siksha2.models.KeywordDist
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.dto.LeaveReviewResult
import com.wafflestudio.siksha2.network.dto.ReviewRestaurant
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.ui.common.downloadImageToFile
import com.wafflestudio.siksha2.utils.ImageUtil
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class MenuDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {
    private val _menu = MutableLiveData<Menu>()
    val menu: LiveData<Menu>
        get() = _menu

    private val _menuId = MutableStateFlow<Long?>(null)
    val menuId: StateFlow<Long?> get() = _menuId

    fun setMenuId(newId: Long) {
        _menuId.value = newId
    }

    private val modifiedReviewCache = MutableStateFlow(mapOf<Long, Review>())

    private val refreshTrigger = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _reviewPagingData: Flow<PagingData<Review>> =
        combine(_menuId.filterNotNull(), refreshTrigger) { id, _ -> id }
            .flatMapLatest { id ->
                Pager(
                    config = MenuReviewPagingSource.Config,
                    pagingSourceFactory = {
                        menuRepository.getReviewsPagingSource(id)
                    }
                ).flow
            }.cachedIn(viewModelScope)

    val reviewPagingData =
        combine(_reviewPagingData, modifiedReviewCache) { pagingData, modifiedReviews ->
            pagingData.map { review ->
                modifiedReviews[review.id] ?: review
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewPhotoPagingData: Flow<PagingData<Review>> =
        combine(_menuId.filterNotNull(), refreshTrigger) { id, _ -> id }
            .distinctUntilChanged()
            .flatMapLatest { id ->
                menuRepository.getPagedReviewsOnlyHaveImagesByMenuIdFlow(id)
            }.cachedIn(viewModelScope)

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

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String>
        get() = _comment

    private val _deleteResult = MutableSharedFlow<Boolean>()
    val deleteResult = _deleteResult.asSharedFlow()

    private val _editingReviewId = MutableStateFlow<Long?>(null)
    val editingReviewId: StateFlow<Long?>
        get() = _editingReviewId

    fun setEditingReview(context: Context, review: Review?) {
        viewModelScope.launch {
            if (review == null) {
                _editingReviewId.value = null
                _reviewRating.floatValue = 5f
                _selectedKeywordList.value = listOf("", "", "")
                _comment.value = ""
                _imageUriList.value = emptyList()
                return@launch
            }

            _editingReviewId.value = review.id
            _reviewRating.floatValue = review.score.toFloat()
            _selectedKeywordList.value = listOf(
                review.keywordReviews.getOrNull(0) ?: "",
                review.keywordReviews.getOrNull(1) ?: "",
                review.keywordReviews.getOrNull(2) ?: ""
            )
            _comment.value = review.comment.orEmpty()

            val imageUris = mutableListOf<Uri>()
            review.etc.images
                ?.take(3) // 최대 3장 제한
                ?.forEach { imageUrl ->
                    val file = downloadImageToFile(context, imageUrl)

                    if (file != null) {
                        val contentUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )

                        imageUris.add(contentUri)
                    }
                }
            _imageUriList.value = imageUris
        }
    }

    fun setComment(text: String) {
        _comment.value = text
    }

    fun refreshMenu(menuId: Long) {
        _networkResultState.value = State.LOADING
        viewModelScope.launch {
            val result = menuRepository.getMenuById(menuId)

            when (result) {
                is NetworkResult.Success -> {
                    _menu.value = result.body
                    modifiedReviewCache.value = emptyMap()
                    _networkResultState.value = State.SUCCESS
                    refreshKeywordDistribution(menuId)
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
                        if (i < data.result.size && data.result[i].etc.images?.isNotEmpty() == true) {
                            urlList.add(data.result[i].etc.images!![0])
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

    fun deleteReview(id: Long) {
        viewModelScope.launch {
            val success = menuRepository.deleteReview(id)
            _deleteResult.emit(success)
        }
    }

    fun getMyReviews(): Flow<PagingData<ReviewRestaurant>> {
        return menuRepository.getMyPagedReviewsByMenuIdFlow()
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
                else -> _keywordDistribution.value = KeywordDist.Empty
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
        _leaveReviewState.value = ReviewState.SUCCESS
    }

    fun notifySendReviewWaiting() {
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

    suspend fun toggleReviewLike(review: Review): NetworkResult<Unit> {
        val reviewUpdateResponse = when (review.isLiked) {
            true -> menuRepository.unlikeReviewById(review.id)
            false -> menuRepository.likeReviewById(review.id)
        }
        when (reviewUpdateResponse) {
            is NetworkResult.Success -> {
                modifiedReviewCache.value = modifiedReviewCache.value.toMutableMap().apply {
                    put(
                        review.id,
                        review.copy(
                            isLiked = !review.isLiked,
                            likeCount = if (review.isLiked) review.likeCount?.minus(1) else review.likeCount?.plus(1)
                        )
                    )
                }
            }
            else -> { }
        }
        return reviewUpdateResponse
    }

    suspend fun leaveReview(context: Context): NetworkResult<LeaveReviewResult>? {
        val reviewId = _editingReviewId.value
        val menuId = _menu.value?.id ?: return null
        val score = reviewRating.floatValue.toLong()
        val taste = selectedKeywordList.value[0]
        val price = selectedKeywordList.value[1]
        val foodComposition = selectedKeywordList.value[2]
        val comment = if (comment.value == "") commentHint.value ?: "" else comment.value

        val imageParts = _imageUriList.value
            ?.takeIf { it.isNotEmpty() }
            ?.map {
                ImageUtil.getCompressedImage(context, it)
            }?.map { file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestBody)
            }

        val commentPart = MultipartBody.Part.createFormData("comment", comment)

        val response = if (imageParts != null) {
            context.showToast("이미지 압축 중입니다.")
            _leaveReviewState.value = ReviewState.COMPRESSING

            if (reviewId != null) {
                menuRepository.patchMenuReview(
                    _editingReviewId.value!!,
                    menuId,
                    score,
                    taste,
                    price,
                    foodComposition,
                    commentPart,
                    imageParts
                )
            } else {
                menuRepository.leaveMenuReviewImage(
                    menuId,
                    score,
                    taste,
                    price,
                    foodComposition,
                    commentPart,
                    imageParts
                )
            }
        } else {
            if (reviewId != null) {
                menuRepository.patchMenuReview(
                    _editingReviewId.value!!,
                    menuId,
                    score,
                    taste,
                    price,
                    foodComposition,
                    commentPart,
                    emptyList()
                )
            } else {
                menuRepository.leaveMenuReview(
                    menuId,
                    score,
                    taste,
                    price,
                    foodComposition,
                    comment
                )
            }
        }
        when (response) {
            is NetworkResult.Success -> {
                notifySendReviewEnd()
                refreshTrigger.value = !refreshTrigger.value
            }
            else -> notifySendReviewWaiting()
        }
        _editingReviewId.value = null

        return response
    }

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }

    enum class ReviewState {
        WAITING,
        COMPRESSING,
        SUCCESS
    }
}
