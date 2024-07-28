package com.wafflestudio.siksha2.ui.menuDetail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.utils.ImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MenuDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    companion object {
        private const val MAX_IMAGE_COUNT = 3
    }

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

    private val _images = MutableStateFlow(emptyList<CompressedImageUiState>())
    val images: StateFlow<List<CompressedImageUiState>> = _images

    private val _imageUrlList = MutableLiveData<List<String>>()
    val imageUrlList: LiveData<List<String>>
        get() = _imageUrlList

    private val _imageCount = MutableLiveData<Long>(0)
    val imageCount: LiveData<Long>
        get() = _imageCount

    private val _leaveReviewState = MutableLiveData<ReviewState>(ReviewState.WAITING)
    val leaveReviewState: LiveData<ReviewState>
        get() = _leaveReviewState

    fun refreshMenu(menuId: Long) {
        _networkResultState.value = State.LOADING
        viewModelScope.launch {
            try {
                _menu.value = menuRepository.getMenuById(menuId)
                _networkResultState.value = State.SUCCESS
            } catch (e: IOException) {
                _networkResultState.value = State.FAILED
            }
        }
    }

    fun refreshImages(menuId: Long) {
        viewModelScope.launch {
            try {
                val data = menuRepository.getFirstReviewPhotoByMenuId(menuId)
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
            } catch (e: IOException) {
                _imageUrlList.value = emptyList()
                _networkResultState.value = State.FAILED
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
            try {
                _commentHint.value = menuRepository.getReviewRecommendationComments(score)
            } catch (e: IOException) {
                _commentHint.value = ""
            }
        }
    }

    fun refreshReviewDistribution(menuId: Long) {
        viewModelScope.launch {
            try {
                _reviewDistribution.value = menuRepository.getReviewDistribution(menuId)
            } catch (e: IOException) {
                _reviewDistribution.value = emptyList()
            }
        }
    }

    fun addImageUri(context: Context, imageUri: Uri, onFailure: () -> Unit) {
        if (images.value.size >= MAX_IMAGE_COUNT) {
            onFailure()
            return
        }

        viewModelScope.launch {
            val compressing = CompressedImageUiState.Compressing(imageUri)

            _images.emit(
                _images.value.toMutableList().apply {
                    add(compressing)
                }
            )

            val compressedImageFile = withContext(Dispatchers.IO) {
                ImageUtil.getCompressedImage(context, imageUri)
            }

            _images.emit(
                _images.value.toMutableList().apply {
                    set(
                        indexOf(compressing),
                        CompressedImageUiState.Completed(
                            compressedImageUri = Uri.fromFile(compressedImageFile),
                            compressedImageFile = compressedImageFile
                        )
                    )
                }
            )
        }
    }

    fun deleteImageUri(index: Int, onFailure: () -> Unit = {}) {
        if (index >= images.value.size) {
            onFailure()
            return
        }
        _images.value = images.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun refreshUriList() {
        _images.value = emptyList()
    }

    fun notifySendReviewEnd() {
        _leaveReviewState.value = ReviewState.WAITING
    }

    suspend fun toggleLike(id: Long, isCurrentlyLiked: Boolean) {
        val updatedMenu = when (isCurrentlyLiked) {
            true -> menuRepository.unlikeMenuById(id)
            false -> menuRepository.likeMenuById(id)
        }
        _menu.postValue(updatedMenu)
    }

    suspend fun leaveReview(score: Double, comment: String, onFailure: () -> Unit) {
        val menuId = menu.value?.id ?: return
        if (images.value.isEmpty()) {
            leaveReviewWithoutImage(menuId, score, comment)
        } else {
            if (images.value.all { it is CompressedImageUiState.Completed }.not()) {
                onFailure()
                return
            }
            leaveReviewWithImage(menuId, score, comment, images.value)
        }
    }

    private suspend fun leaveReviewWithoutImage(menuId: Long, score: Double, comment: String) {
        menuRepository.leaveMenuReview(menuId, score, comment)
    }

    private suspend fun leaveReviewWithImage(menuId: Long, score: Double, comment: String, images: List<CompressedImageUiState>) {
        val commentBody = MultipartBody.Part.createFormData("comment", comment)
        val imagesBody = withContext(Dispatchers.Default) {
            images.map {
                val compressedImageFile = (it as CompressedImageUiState.Completed).compressedImageFile
                val requestBody = compressedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", compressedImageFile.name, requestBody)
            }
        }
        menuRepository.leaveMenuReviewImage(menuId, score.toLong(), commentBody, imagesBody)
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

sealed interface CompressedImageUiState {
    class Compressing(val originalImageUri: Uri) : CompressedImageUiState
    class Completed(
        val compressedImageUri: Uri,
        val compressedImageFile: File,
    ) : CompressedImageUiState
}
